package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameConstants;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server{
    private static ServerSocket server;
    private static ConnectionListener connectionListener;
    private ObjectOutputStream outgoing;
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

        }
    }

    /**
     * A utility inner class that contains the available replies that gives.
     */
    private static class ReplyHandler{
        public static void sendMessageToClients(ConnectionHandler client, String message){

        }

        public static void closeConnection(Socket client, String[]data){
            try{
                ObjectOutputStream stream = new ObjectOutputStream(client.getOutputStream());
                stream.writeObject(data);
                stream.flush();

                stream.close();
                client.close();
            }catch(Exception e){

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
                    Socket client = server.accept();
                    if(GameConstants.MAX_PLAYERS >= connections.size()){
                        ReplyHandler.closeConnection(client, new String[]{GameConstants.REQUEST_DISCONNECT, "Paskaa perseeseen", "SERVER"});
                    }
                    else{
                        connections.add(new ConnectionHandler(client));
                        MainController.chatMessageReceived(client.getInetAddress().getHostAddress() + " connected.", "SERVER");
                        connectionPool.execute(connections.get(connections.size() - 1));
                    }
                }
            }catch(Exception  e){

            }
        }
    }
}
