package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;

import java.io.*;
import java.net.Socket;

public class Client {
    private static boolean running;
    private static Socket connection;
    private static PrintWriter out;
    private static BufferedReader in;
    private static String address;
    private static ServerListener listener;

    public static boolean connectToServer(String ad) {
        try {
            if (!running) {
                address = ad;
                connection = new Socket(address, GameConstants.PORT);
                if (connection.isConnected()) {
                    connection.setKeepAlive(true);
                    connection.setTcpNoDelay(true);
                    connection.setSoLinger(false, 0);
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    out = new PrintWriter(connection.getOutputStream(), true);
                    listener = new ServerListener();
                    sendJoinRequest();
                    return true;
                }
            } else {
                System.out.println("Already connected to server");
            }
        } catch (Exception e) {
            disconnectFromServer();
            e.printStackTrace();
        }
        return false;
    }

    private static void sendJoinRequest() {
        MessageObject message = new MessageObject(GameConstants.REQUEST_JOIN, "", GameAssets.getNickname());
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendReadyMessage() {
        MessageObject message = new MessageObject(GameConstants.REQUEST_READY, "", GameAssets.getNickname());
        out.println(GameAssets.getGson().toJson(message));
    }

    /**
     * Used for sending chat messages and also for initiating rematches.
     * @param text
     */
    public static void sendChatMessage(String text) {
        MessageObject message;
        if(GameAssets.getState() == GameConstants.STATE_POST_MATCH && text.equals("/rematch")){
            message = new MessageObject(GameConstants.REQUEST_REMATCH_YES, "", GameAssets.getNickname());
        }
        else{
            message = new MessageObject(GameConstants.REQUEST_MESSAGE, text, GameAssets.getNickname());
        }
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendTargetMessage(int x, int y) {
        MessageObject message = new MessageObject(GameConstants.REQUEST_ENDTURN, String.valueOf(GameAssets.getPlayerId()), GameAssets.getNickname());
        message.setX(x);
        message.setY(y);
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendHitMessage(int x, int y) {
        MessageObject message = new MessageObject(GameConstants.REQUEST_HIT, "", GameAssets.getNickname());
        message.setX(x);
        message.setY(y);
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendSinkMessage(int shipIndex) {
        MessageObject message = new MessageObject(GameConstants.REQUEST_SUNK,
                GameConstants.SHIP_NAMES[shipIndex], String.valueOf(GameAssets.getPlayerId()));
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendMissMessage(int x, int y) {
        MessageObject message = new MessageObject(GameConstants.REQUEST_MISS, "", GameAssets.getNickname());
        message.setX(x);
        message.setY(y);
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void sendGameOverMessage() {
        MessageObject message = new MessageObject(GameConstants.REQUEST_ALL_DESTROYED, "", "");
        message.setId(GameAssets.getPlayerId());
        out.println(GameAssets.getGson().toJson(message));
    }

    public static void disconnectFromServer() {
        try {
            if (listener != null) {
                listener.stopListening();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ServerListener extends Thread {
        String readData = "";

        public ServerListener() {
            running = true;
            this.start();
        }

        private void parseServerInput(MessageObject data) {
            switch (data.getType()) {
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
                    MainController.checkBeginTurnProcessShot(data);
                    break;
                case GameConstants.REQUEST_HIT:
                    MainController.refreshHits(GameConstants.TILE_STATE_HIT, data.getX(), data.getY());
                    break;
                case GameConstants.REQUEST_MISS:
                    MainController.refreshHits(GameConstants.TILE_STATE_MISS, data.getX(), data.getY());
                    break;
                case GameConstants.REQUEST_SUNK:
                    MainController.sunkShip(data);
                    break;
                case GameConstants.REQUEST_ALL_DESTROYED:
                    MainController.statePostMatch(data);
                    break;
            }
        }

        public void stopListening() {
            running = false;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    while ((readData = in.readLine()) != null) {
                        parseServerInput(GameAssets.getGson().fromJson(readData, MessageObject.class));
                    }
                    if (in.read() == -1) {
                        MainController.showMessage(GameConstants.INFO_DISCONNECTED + address, GameConstants.CLIENT_NAME);
                        disconnectFromServer();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
