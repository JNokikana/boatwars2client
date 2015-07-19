package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread{
    private static boolean running;
    private static Socket connection;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String address;
    private static ServerListener listener;

    public static boolean connectToServer(String ad){
        try{
            if(!running){
                connection = new Socket(address, GameConstants.PORT);
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                address = ad;
                listener = new ServerListener();
                return true;
            }
            else{
                System.out.println("Already connected to server");
            }
        }catch(Exception e){

        }
        return false;
    }

    public static void disconnectFromServer(){
        try{
            listener.stopListening();
            in.close();
            connection.close();
            MainController.chatMessageReceived("Disconnecting..", "CLIENT");
        }catch(Exception e){

        }
    }

    private static class ServerListener extends Thread{
        String[] input = null;

        public ServerListener(){
            running = true;
            start();
        }

        private void parseJPData(String[] data){
            if(data.length == 3){
                switch(data[0]){
                    case GameConstants.REQUEST_MESSAGE:
                        MainController.chatMessageReceived(data[1], data[2]);
                        break;
                    case GameConstants.REQUEST_CLIENT_BYE:
                        MainController.chatMessageReceived(data[2] + " has " + data[1], "SERVER");
                        break;
                    case GameConstants.REQUEST_PLAYER:
                        String message = (data[1]).substring(0, data[1].length() - 1);
                        MainController.chatMessageReceived(message, data[2]);
                        GameAssets.setPlayerId(Integer.valueOf(data[1].substring(data[1].length() - 1)).byteValue());
                        MainController.chatMessageReceived("You are player " + (GameAssets.getPlayerId() + 1) + ".", "SERVER");
                        MainController.stateGameReady();
                        break;
                    case GameConstants.REQUEST_READY:
                        MainController.chatMessageReceived("Player " + data[2] + " is ready.", "SERVER");
                        break;
                    case GameConstants.REQUEST_BEGIN:
                        MainController.chatMessageReceived("All players are ready. Let the boat wars begin!!", "SERVER");
                        MainController.stateGameBegun();
                        break;
                    case GameConstants.REQUEST_ENDTURN:
                        if(!data[2].equals(String.valueOf(GameAssets.getPlayerId()))){
                            MainController.processShot(data[1]);
                            MainController.beginTurn();
                        }
                        break;
                    case GameConstants.REQUEST_HIT:
                        MainController.refreshHits(GameConstants.TILE_STATE_HIT, data[1], data[2]);
                        break;
                    case GameConstants.REQUEST_MISS:
                        MainController.refreshHits(GameConstants.TILE_STATE_MISS, data[1], data[2]);
                        break;
                    case GameConstants.REQUEST_SUNK:
                        if(Integer.valueOf(data[2]) == GameAssets.getPlayerId()){
                            MainController.chatMessageReceived("Your " + data[1] + " was sunk!", "SERVER");
                        }
                        else{
                            MainController.chatMessageReceived("You sunk the enemy " + data[1] + "!", "SERVER");
                        }
                        break;
                    case GameConstants.REQUEST_ALL_DESTROYED:
                        if(Integer.valueOf(data[2]) != GameAssets.getPlayerId()){
                            MainController.chatMessageReceived("The BoatWar is over. You rule!", "SERVER");
                            GameAssets.setGameResult(true);
                        }
                        else{
                            MainController.chatMessageReceived("The BoatWar is over. You suck!", "SERVER");
                            GameAssets.setGameResult(false);
                        }
                        MainController.statePostMatch();
                        break;
                }
            }
        }

        public void stopListening(){
            running = false;
        }

        @Override
        public void run(){
            try{
                while (running) {
                    if(in.read() != - 1){
                        input = (String[])in.readObject();
                        parseJPData(input);
                    }
                    else{
                        disconnectFromServer();
                    }
                }
            }
            catch(Exception e){

            }
        }
    }
    
    public void sendData(String[] data){
        try{
            if(!running){
                out.writeObject(data);
                out.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
