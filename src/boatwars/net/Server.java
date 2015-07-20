package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server{
    private static ServerSocket server;
    private static ConnectionListener connectionListener;
    private static ExecutorService connectionPool;
    private static List<ConnectionHandler> connections;
    private static boolean listening;

    public static void init(){
        try{
            connections = new ArrayList<ConnectionHandler>();
            connectionListener = new ConnectionListener();
            connectionPool = Executors.newFixedThreadPool(GameConstants.MAX_PLAYERS);
            connectionListener.start();
        }catch(Exception e){
            shutdown();
            e.printStackTrace();
        }
    }

    public static List<ConnectionHandler> getConnections(){
        return connections;
    }

    public static boolean isRunning(){
        return listening;
    }

    public static void shutdown(){
        try{
            connectionListener.stopListening();
            server.close();
            connectionPool.shutdown();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * A utility inner class that contains the available replies that the server gives.
     */
    private static class ReplyHandler{

        public static void broadcastToAll(List<ConnectionHandler> clients, String type, String text){
            System.out.println("PASkaa");
            MessageObject message = new MessageObject(type, text, "SERVER");
            for(int i = 0; i < clients.size(); i ++){
                clients.get(i).getOutput().printf(GameAssets.getGson().toJson(message));
            }
        }

        public static void closeConnection(ConnectionHandler client, String message){
            try{
                MessageObject object = new MessageObject(GameConstants.REQUEST_DISCONNECT, message, "SERVER");
                client.getOutput().printf(GameAssets.getGson().toJson(object));
                client.disconnectFromClient();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * The listener thread for the server that waits for incoming client connections.
     */
    private static class ConnectionListener extends Thread{

        private void startListening() throws Exception{
            server = new ServerSocket(GameConstants.PORT);
            listening = true;
        }

        private void stopListening() throws Exception{
            listening = false;
        }

        @Override
        public void run(){
            try{
                startListening();

                while(listening){
                    ConnectionHandler client = new ConnectionHandler(server.accept());
                    if(connections.size() >= GameConstants.MAX_PLAYERS){
                        ReplyHandler.closeConnection(client, "Paskaa perseeseen");
                    }
                    else{
                        connections.add(client);
                        MainController.chatMessageReceived(client.getClient().getInetAddress().getHostAddress() + " connected.", "SERVER");
                        connectionPool.execute(client);
                        ReplyHandler.broadcastToAll(connections, GameConstants.REQUEST_INFO, "Biiiitch!");
                    }
                }
            }catch(Exception  e){
                shutdown();
                e.printStackTrace();
            }
        }
    }
}
