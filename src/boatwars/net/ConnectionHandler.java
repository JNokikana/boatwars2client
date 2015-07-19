package boatwars.net;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread{
    private Socket client;
    private boolean running;
    private ObjectInputStream input;
    
    public ConnectionHandler(Socket client){
        try{
            this.client = client;
            input = new ObjectInputStream(client.getInputStream());
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

    public void handleRequest(String [] data){

    }
    
    @Override
    public void run(){
        try{

            while(running){
                if(input.read() != -1){
                    String[] inputMessage = null;
                    inputMessage = (String[])input.readObject();
                    handleRequest(inputMessage);
                }
                else{
                    disconnectFromClient();
                }
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
