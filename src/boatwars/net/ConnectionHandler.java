package boatwars.net;

import boatwars.util.GameAssets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler extends Thread{
    private Socket client;
    private boolean running;
    private BufferedReader input;
    private PrintWriter output;
    private String readData;
    
    public ConnectionHandler(Socket client){
        try{
            this.client = client;
            input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            output = new PrintWriter(client.getOutputStream(), true);
            running = true;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public Socket getClient(){
        return this.client;
    }

    public synchronized PrintWriter getOutput(){
        return output;
    }

    public void disconnectFromClient() throws Exception{
        input.close();
        client.close();
        running = false;
    }

    public void handleRequest(MessageObject data){
        System.out.println("asd");
        System.out.println(data.getMessage());
    }
    
    @Override
    public void run(){
        try{

            while(running){
                while((readData = input.readLine()) != null){
                    System.out.println(readData);
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
