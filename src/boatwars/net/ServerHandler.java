package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameConstants;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerHandler extends Thread{
    private Socket client;
    private Server server;
    private boolean running;
    private String clientIP;
    private ObjectInputStream input;
    private MainController control;
    
    public ServerHandler(Server server, MainController control){
        this.control = control;
        this.server = server;
        running = true;
    }

    public void setClient(Socket c){
        this.client = c;
        this.clientIP = c.getInetAddress().getHostAddress();
    }
    
    public Socket getClient(){
        return this.client;
    }
    
    @Override
    public void run(){
        try{
            while(running){
                input = new ObjectInputStream(client.getInputStream());
                String[] inputMessage = null;
                inputMessage = (String[])input.readObject();
                if(!server.isOffline()){
                    if(inputMessage[0].equals(GameConstants.REQUEST_CLIENT_BYE)){
                        stopRunning();
                    }
                    else if(inputMessage[0].equals(GameConstants.REQUEST_READY)){
                        server.incSetupComplete();
                        if(server.getSetupCompleteNumber() >= 2){
                            inputMessage[0] = GameConstants.REQUEST_BEGIN;
                        }
                    }
                    server.receiveJPData(inputMessage);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void stopRunning(){
        try{
            client.close();
            input.close();
            this.running = false;
        }catch(Exception e){}
    }
}
