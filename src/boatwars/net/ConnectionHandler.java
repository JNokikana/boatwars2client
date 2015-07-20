package boatwars.net;

import boatwars.util.GameAssets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread{
    private Socket client;
    private boolean running;
    private BufferedReader input;
    private String readData;
    
    public ConnectionHandler(Socket client){
        try{
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            running = true;
        }catch(Exception e){

        }
    }

    public void setClient(Socket c){
        this.client = c;
    }
    
    public Socket getClient(){
        return this.client;
    }

    public void disconnectFromClient() throws Exception{
        input.close();
        client.close();
        running = false;
    }

    public void handleRequest(MessageObject data){

    }
    
    @Override
    public void run(){
        try{

            while(running){
                while((readData = input.readLine()) != null){
                    handleRequest(GameAssets.getGson().fromJson(readData, MessageObject.class));
//                if (inputMessage[0].equals(GameConstants.REQUEST_CLIENT_BYE)) {
//                    stopRunning();
//                } else if (inputMessage[0].equals(GameConstants.REQUEST_READY)) {
//                    server.incSetupComplete();
//                    if (server.getSetupCompleteNumber() >= 2) {
//                        inputMessage[0] = GameConstants.REQUEST_BEGIN;
//                    }
//                }
//                server.receiveJPData(inputMessage);
                }

                if(input.read() == -1){
                    disconnectFromClient();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
