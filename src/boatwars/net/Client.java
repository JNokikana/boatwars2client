package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;

import java.io.*;
import java.net.Socket;

public class Client{
    private static boolean running;
    private static Socket connection;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String address;
    private static ServerListener listener;

    public static boolean connectToServer(String ad){
        try{
            if(!running){
                address = ad;
                connection = new Socket(address, GameConstants.PORT);
                if(connection.isConnected()){
                    connection.setKeepAlive(true);
                    connection.setTcpNoDelay(true);
                    connection.setSoLinger(false, 0);
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    out = new PrintWriter(connection.getOutputStream(), true);
                    listener = new ServerListener();
                    sendJoinRequest();
                    return true;
                }
            }
            else{
                System.out.println("Already connected to server");
            }
        }catch(Exception e){
            disconnectFromServer();
            e.printStackTrace();
        }
        return false;
    }

    private static void sendJoinRequest(){
        MessageObject message = new MessageObject(GameConstants.REQUEST_JOIN, "", GameAssets.getNickname());
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendReadyMessage(){
        MessageObject message = new MessageObject(GameConstants.REQUEST_READY, "", GameAssets.getNickname());
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendChatMessage(String text){
        MessageObject message = new MessageObject(GameConstants.REQUEST_MESSAGE, text, GameAssets.getNickname());
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendTargetMessage(int x, int y){
        MessageObject message = new MessageObject(GameConstants.REQUEST_ENDTURN, "", GameAssets.getNickname());
        message.setX(x);
        message.setY(y);
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void disconnectFromServer(){
        try{
            if(listener != null){
                listener.stopListening();
            }
            if(connection != null){
                connection.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static class ServerListener extends Thread{
        String readData = "";

        public ServerListener(){
            running = true;
            this.start();
        }

        private void parseServerInput(MessageObject data){
            switch(data.getType()){
                case GameConstants.REQUEST_INFO:
                    MainController.showMessage(data.getMessage(), data.getSender());
                    break;
                case GameConstants.REQUEST_BEGIN:
                    MainController.stateGameBegin(data.getMessage());
                    break;
                case GameConstants.REQUEST_GAMEPLAY_START:
                    MainController.stateGameRunning();
                    break;
                case GameConstants.REQUEST_MESSAGE:
                    MainController.showMessage(data.getMessage(), data.getSender());
                    break;
                case GameConstants.REQUEST_ENDTURN:
                    MainController.checkBeginTurn(data.getMessage());
            }
//            if(data.length == 3){
//                switch(data[0]){
//                    case GameConstants.REQUEST_MESSAGE:
//                        MainController.showMessage(data[1], data[2]);
//                        break;
//                    case GameConstants.REQUEST_CLIENT_BYE:
//                        MainController.showMessage(data[2] + " has " + data[1], "SERVER");
//                        break;
//                    case GameConstants.REQUEST_PLAYER:
//                        String message = (data[1]).substring(0, data[1].length() - 1);
//                        MainController.showMessage(message, data[2]);
//                        GameAssets.setPlayerId(Integer.valueOf(data[1].substring(data[1].length() - 1)).byteValue());
//                        MainController.showMessage("You are player " + (GameAssets.getPlayerId() + 1) + ".", "SERVER");
//                        MainController.stateGameBegin();
//                        break;
//                    case GameConstants.REQUEST_READY:
//                        MainController.showMessage("Player " + data[2] + " is ready.", "SERVER");
//                        break;
//                    case GameConstants.REQUEST_BEGIN:
//                        MainController.showMessage("All players are ready. Let the boat wars begin!!", "SERVER");
//                        MainController.stateGameRunning();
//                        break;
//                    case GameConstants.REQUEST_ENDTURN:
//                        if(!data[2].equals(String.valueOf(GameAssets.getPlayerId()))){
//                            MainController.processShot(data[1]);
//                            MainController.checkBeginTurn();
//                        }
//                        break;
//                    case GameConstants.REQUEST_HIT:
//                        MainController.refreshHits(GameConstants.TILE_STATE_HIT, data[1], data[2]);
//                        break;
//                    case GameConstants.REQUEST_MISS:
//                        MainController.refreshHits(GameConstants.TILE_STATE_MISS, data[1], data[2]);
//                        break;
//                    case GameConstants.REQUEST_SUNK:
//                        if(Integer.valueOf(data[2]) == GameAssets.getPlayerId()){
//                            MainController.showMessage("Your " + data[1] + " was sunk!", "SERVER");
//                        }
//                        else{
//                            MainController.showMessage("You sunk the enemy " + data[1] + "!", "SERVER");
//                        }
//                        break;
//                    case GameConstants.REQUEST_ALL_DESTROYED:
//                        if(Integer.valueOf(data[2]) != GameAssets.getPlayerId()){
//                            MainController.showMessage("The BoatWar is over. You rule!", "SERVER");
//                            GameAssets.setGameResult(true);
//                        }
//                        else{
//                            MainController.showMessage("The BoatWar is over. You suck!", "SERVER");
//                            GameAssets.setGameResult(false);
//                        }
//                        MainController.statePostMatch();
//                        break;
//                }
//            }

        }

        public void stopListening(){
            running = false;
        }

        @Override
        public void run(){
            try{
                while (running) {
                    while((readData = in.readLine()) != null){
                        parseServerInput(GameAssets.getGson().fromJson(readData, MessageObject.class));
                    }
                    if (in.read() == -1) {
                        MainController.showMessage(GameConstants.INFO_DISCONNECTED + address, GameConstants.CLIENT_NAME);
                        disconnectFromServer();
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
