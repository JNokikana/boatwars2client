package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameConstants;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread{
    private ServerSocket server;
    private MainController controller;
    private ObjectOutputStream outgoing;
    private ServerHandler [] handler;
    private boolean listening;
    public byte setupComplete;
    
    public Server(MainController controller){
        listening = true;
        try{
            server = new ServerSocket(GameConstants.PORT);   
        }catch(Exception e){
            e.printStackTrace();
        }
        this.controller = controller;
        handler = new ServerHandler[2];
    }
    
    @Override
    public void run(){
        startServer();
    }
    
    public void startServer(){
        try{
            System.out.println("Waiting for connections...");
            int i = 0;
            while(listening){
                Socket client = server.accept();
                controller.chatMessageReceived(client.getInetAddress().getHostAddress() + " connected.", "SERVER");
                handler[i] = new ServerHandler(this, controller);
                handler[i].setClient(client);
                handler[i].start();
                i ++;
                if(i >= handler.length){
                    listening = false;
                    controller.chatMessageReceived("Server running on " + server.getInetAddress().getHostAddress() + ".", "SERVER");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        for(int i = 0; i < handler.length; i ++){
            if(handler[i] != null){
                try{
                    outgoing = new ObjectOutputStream(handler[i].getClient().getOutputStream());
                    outgoing.writeObject(new String[]{GameConstants.REQUEST_PLAYER, GameConstants.SERVER_CONNECTION_ON + i, "SERVER"});
                    outgoing.flush();
                    outgoing = new ObjectOutputStream(handler[i].getClient().getOutputStream());
                    outgoing.writeObject(new String[]{GameConstants.REQUEST_MESSAGE, "Game has started. Place your boats!", "SERVER"});
                    outgoing.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    public synchronized byte getSetupCompleteNumber(){
        return this.setupComplete;
    }
    
    public synchronized void incSetupComplete(){
        this.setupComplete ++;
    }
    
    public synchronized boolean isOffline(){
        return this.server.isClosed();
    }
    
    public void disconnect(){
        try{
            sendToClients(new String[]{GameConstants.REQUEST_DISCONNECT, "Server disconnected", "SERVER"});
            if(outgoing == null){
                controller.chatMessageReceived("Server stopped.", "SERVER");
                server.close();
            }
            else{
                outgoing.close();
                server.close();
                for (int i = 0; i < handler.length; i++) {
                    if (handler[i] != null) {
                        handler[i].stopRunning();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /**
     * Sends data to all connected clients.
     * @param data 
     */
    private void sendToClients(String[] data){
        for(int i = 0; i < handler.length; i ++){
            if(handler[i] != null){
                if(!handler[i].getClient().isClosed() && handler[i].isAlive()){
                    try{
                        outgoing = new ObjectOutputStream(handler[i].getClient().getOutputStream());
                        outgoing.writeObject(data);
                        outgoing.flush();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public synchronized void receiveJPData(String[] data){
        switch(data[0]){
            case GameConstants.REQUEST_MESSAGE:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_DISCONNECT:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_CLIENT_BYE:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_READY:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_ENDTURN:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_BEGIN:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_HIT:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_MISS:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_SUNK:
                sendToClients(data);
                break;
            case GameConstants.REQUEST_ALL_DESTROYED:
                sendToClients(data);
                break;
        }
    }
}
