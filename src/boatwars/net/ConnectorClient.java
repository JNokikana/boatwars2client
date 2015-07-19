package boatwars.net;

import boatwars.controller.MainController;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectorClient extends Thread{
    private boolean running;
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private MainController controller;
    private GameAssets assets;
    private String address;
    
    public ConnectorClient(MainController controller, GameAssets assets, String address){
        this.assets = assets;
        this.address = address;
        this.controller = controller;
        running = true;
    }
    
    public void setConnection(Socket c){
        this.connection = c;
    }
    
    public Socket getConnection(){
        return this.connection;
    }
    
    @Override
    public void run(){
        String[] input = null;
        try{
            connection = new Socket(address, GameConstants.PORT);
            while(running){
                in = new ObjectInputStream(connection.getInputStream());
                input = (String[])in.readObject();
                parseJPData(input);
            }
        }catch(Exception e){
            controller.errorConnecting();
            e.printStackTrace();
        }
    }
    
    public void sendData(String[] data){
        try{
            if(!connection.isClosed()){
                out = new ObjectOutputStream(connection.getOutputStream());
                out.writeObject(data);
                out.flush();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void disconnect(){
        try{
            sendData(new String[]{GameConstants.REQUEST_CLIENT_BYE, "disconnected", assets.getNickname()});
            controller.chatMessageReceived("Disconnected from server", "CLIENT");
            connection.shutdownInput();
            connection.shutdownOutput();
            connection.close();
            in.close();
            out.close();
            running = false;
        }catch(Exception e){}
    }
    
    private void parseJPData(String[] data){
        if(data.length == 3){
            switch(data[0]){
                case GameConstants.REQUEST_MESSAGE:
                    controller.chatMessageReceived(data[1], data[2]);
                    break;
                case GameConstants.REQUEST_DISCONNECT:
                    controller.chatMessageReceived(data[1], data[2]);
                    if(!assets.isHost()){
                        controller.disconnectFromServer();   
                    }
                    break;
                case GameConstants.REQUEST_CLIENT_BYE:
                    controller.chatMessageReceived(data[2] + " has " + data[1], "SERVER");
                    break;
                case GameConstants.REQUEST_PLAYER:
                    String message = (data[1]).substring(0, data[1].length() - 1);
                    controller.chatMessageReceived(message, data[2]);
                    assets.setPlayerId(Integer.valueOf(data[1].substring(data[1].length() - 1)).byteValue());
                    controller.chatMessageReceived("You are player " + (assets.getPlayerId() + 1) + ".", "SERVER");
                    controller.stateGameReady();
                    break;
                case GameConstants.REQUEST_READY:
                    controller.chatMessageReceived("Player " + data[2] + " is ready.", "SERVER");
                    break;
                case GameConstants.REQUEST_BEGIN:
                    controller.chatMessageReceived("All players are ready. Let the boat wars begin!!", "SERVER");
                    controller.stateGameBegun();
                    break;
                case GameConstants.REQUEST_ENDTURN:
                   if(!data[2].equals(String.valueOf(assets.getPlayerId()))){
                       controller.processShot(data[1]);
                       controller.beginTurn();
                   }
                   break;
                case GameConstants.REQUEST_HIT:
                    controller.playSound(GameConstants.SOUND_HIT);
                    controller.playSound(GameConstants.SOUND_HIT_SHOT);
                    controller.refreshHits(GameConstants.TILE_STATE_HIT, data[1], data[2]);
                    break;
                case GameConstants.REQUEST_MISS:
                    controller.playSound(GameConstants.SOUND_HIT);
                    controller.playSound(GameConstants.SOUND_MISS_SHOT);
                    controller.refreshHits(GameConstants.TILE_STATE_MISS, data[1], data[2]);
                    break;
                case GameConstants.REQUEST_SUNK:
                    controller.playSound(GameConstants.SOUND_SUNK);
                    if(Integer.valueOf(data[2]) == assets.getPlayerId()){
                        controller.chatMessageReceived("Your " + data[1] + " was sunk!", "SERVER");
                    }
                    else{
                        controller.chatMessageReceived("You sunk the enemy " + data[1] + "!", "SERVER");
                    }
                    break;
                case GameConstants.REQUEST_ALL_DESTROYED:
                    if(Integer.valueOf(data[2]) != assets.getPlayerId()){
                        controller.chatMessageReceived("The BoatWar is over. You rule!", "SERVER");
                        assets.setGameResult(true);
                    }
                    else{
                        controller.chatMessageReceived("The BoatWar is over. You suck!", "SERVER");
                        assets.setGameResult(false);
                    }
                    controller.statePostMatch();
                    break;
            }
        }
    }
}
