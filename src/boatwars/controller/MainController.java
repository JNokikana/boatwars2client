package boatwars.controller;

import boatwars.gui.MainGUI;
import boatwars.gui.NewGameJoinGUI;
import boatwars.net.ConnectorClient;
import boatwars.net.Server;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MainController {
    private GameAssets assets;
    private MainGUI gui;
    private String path;
    
    public MainController(MainGUI gui, GameAssets assets){
        try{
            this.path = new File("").getCanonicalPath(); 
        }catch(Exception e){}
        this.gui = gui;
        this.assets = assets;
    }
    
    public void playSound(String g){
        this.assets.getSoundControl().playSound(g);
    }
    
    public void drawPlateScreen(){
        ImageIcon scaledImage = scaleImage(this.gui.getScorePicture(),
                new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_PLATE));
        this.gui.getScorePicture().setIcon(scaledImage);
    }
    
    public void drawShipPlate(){
        ImageIcon scaledImage = scaleImage(gui.getShipScreen(), 
                new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_PLATE));
        gui.getShipScreen().setIcon(scaledImage);
    }
    
    public void keyPress(KeyEvent k){
        if((assets.getState() != GameConstants.STATE_MENU) && 
                k.getKeyCode() == KeyEvent.VK_ENTER
                && gui.getChatField().getText().length() > 0 && gui.getChatField().hasFocus()){
            sendChatMessage(gui.getChatField().getText());
            gui.getChatField().setText(null);
        }
    }
    
    private void showServerNotice(){
        JOptionPane.showMessageDialog(null, "To play online you must open port " + GameConstants.PORT + " "
                + " from your router settings.", "Start Server", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actionHostGame(){
        showServerNotice();
        if(assets.getServer() != null){
            if(!assets.getServer().isOffline()){
                try{
                    stopServer();
                } catch (Exception er) {}
            }
        }
        createServer();
    }
    
    public void actionOrientation(){
        assets.switchOrientation();
        refreshShipScreen();
    }
    
    public void actionJoinGame(){
        new NewGameJoinGUI(this);
    }
    
    public void actionQuitGame(){
        int selected = JOptionPane.showConfirmDialog(null, 
                "Are you sure you would like to quit?", "Quit Game", JOptionPane.YES_NO_OPTION);
        if(selected == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }
    
    public void actionAbout(){
        JOptionPane.showMessageDialog(null, GameConstants.ABOUT, GameConstants.TITLE, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void actionPlaceBoat(){
        if(isValidSpot()){
            assets.setIsPlaced(assets.getSelected(), true, assets.isOriented());
            assets.setShipCoordinates(assets.getMouseXY()[0] * GameConstants.TILE_SIZE,
                    assets.getMouseXY()[1] * GameConstants.TILE_SIZE, assets.getSelected());
            assets.incSelected();
            if(assets.allPlaced()){
                boatSetupComplete();
            } 
            else{
                refreshShipScreen();
            }

            gui.getGamePanel().repaint();
        }
        else{
            errorCannotPlaceShipThere();
        }
    }
    
    public void actionEndTurn(){
        if(gui.getEndTurnButton().getText().equals("Ready!")){
            assets.setState(GameConstants.STATE_WAITING);
            gui.setEndTurn(false);
            sendReadyMessage();
            gui.getEndTurnButton().setText("End Turn");
        } 
        else{
            if(assets.isTargetPlaced()){
                gui.addText("Thus ends your turn!");
                assets.setTargetPlaced(false);
                assets.setTurn(false);
                gui.setEndTurn(false);
                sendEndTurnMessage();
            } 
            else{
                errorNoTarget();
            }
        }               
    }
    
    public void actionPlaceTarget(){
        if(assets.isTurn() && assets.getState() == GameConstants.STATE_GAME){
            int x = assets.getMouseXY()[0] * GameConstants.TILE_SIZE;
            int y = assets.getMouseXY()[1] * GameConstants.TILE_SIZE;
            
            if(isValidTargetSpot(x, y)){
                if(assets.getTargetCoords()[0][0] != x || assets.getTargetCoords()[0][1] != y){
                    assets.setTargetPlaced(true);
                    assets.setTargetCoords(x, y);
                    gui.getGamePanel().repaint();
                } 
                else{
                    errorAlreadyTargeted();
                }
            }
            else{
                errorCannotTargetThere();
            }
        }
    }
    
    /**
     * We check if the targeted tile is free or not and whether it is in the
     * correct area.
     * @param x
     * @param y
     * @return 
     */
    private boolean isValidTargetSpot(int x, int y){
        if(assets.getTileState(x, y) != GameConstants.TILE_STATE_EMPTY){
            return false;
        }
        
        if(assets.getPlayerId() == 0){
            return x < 240;
        } 
        else{
            return x >= 240;
        }
    }
    
    /**
     * Gives the coordinates of the tiles the ship to be placed would occupy.
     * @param mouseX
     * @param mouseY
     * @return 
     */
    private int[][] getSelectedShipCoordinates(int mouseX, int mouseY){
        int [][] shipCoordinates;
        shipCoordinates = new int[GameConstants.SIZES[assets.getSelected()]][2];
        
        for(int i = 0; i < shipCoordinates.length; i++){
            if(assets.isOriented()){
                shipCoordinates[i][0] = mouseX + (GameConstants.TILE_SIZE * i);
                shipCoordinates[i][1] = mouseY;
            } 
            else{
                shipCoordinates[i][0] = mouseX;
                shipCoordinates[i][1] = mouseY + (GameConstants.TILE_SIZE * i);
            }
        }
        
        return shipCoordinates;
    }
    /**
     * Checks whether the ship to be placed collides with the coords in question.
     * @param coords
     * @return 
     */
    private boolean doesShipIntersect(int [][] coords){
        for(int i = 0; i < GameConstants.SHIPS.length; i++){
            if(assets.getIsPlaced()[i][0]){
                for(int m = 0; m < GameConstants.SIZES[i]; m ++){
                    int thisX = assets.getShipCoordinates()[i][0];
                    int thisY = assets.getShipCoordinates()[i][1];
                    /* We check if the ship is oriented. */
                    if(assets.getIsPlaced()[i][1]){
                        thisX += GameConstants.TILE_SIZE * m;
                    } 
                    else{
                        thisY += GameConstants.TILE_SIZE * m;
                    }
                    
                    for(int r = 0; r < coords.length; r ++){
                        if(coords[r][0] == thisX && coords[r][1] == thisY){
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    /**
     * Checks whether this spot is valid for ship placement.
     * @return 
     */
    private boolean isValidSpot(){
        int mouseX = assets.getMouseXY()[0] * GameConstants.TILE_SIZE;
        int mouseY = assets.getMouseXY()[1] * GameConstants.TILE_SIZE;
        
        int [][] shipCoordinates = getSelectedShipCoordinates(mouseX, mouseY);
        
        if(assets.getPlayerId() == 0){
            if(mouseX < 240 || doesShipIntersect(shipCoordinates)){
                return false;
            }
        }
        else{
            if(mouseX >= 240 || doesShipIntersect(shipCoordinates)){
                return false;
            }
        }
        
        return true;
    }
    
    private void errorCannotPlaceShipThere(){
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_CANNOT_PLACE_SHIP, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void errorCannotTargetThere(){
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_CANNOT_TARGET_THERE, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void errorAlreadyTargeted(){
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_ALREADY_TARGETED, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void errorNoTarget(){
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_NO_TARGET, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void boatSetupComplete(){
        gui.stateBoatSetupComplete();
        gui.addText("Boats placed. When ready press 'Ready!'");
    }
    
    private void createServer(){
        gui.stateHostingServer();
        hostGame();
    }
    
    public void attemptConnection(String ip){
        gui.addText("Establishing connection...");
        assets.setClient(new ConnectorClient(this, assets, ip));
        assets.getClient().start();
        gui.stateCanDisconnectFromServer();
    }
    
    public void refreshCursorGraphics(){
        gui.getGamePanel().repaint();
    }
    
    public void hostGame(){
        assets.setServer(new Server(this));
        chatMessageReceived("Server started.", "SERVER");
        chatMessageReceived("Server is waiting for clients to join...", "SERVER");
        assets.getServer().start();
    }
    
    public void setName(String g){
        this.assets.setNickname(g);
    }
    
    public void disconnectFromServer(){
        try{
            assets.getClient().disconnect();
            assets.setClient(null);
        }catch(Exception e){}
        
        gui.stateDisconnectedFromServer();
        gui.disableChat();
    }
    
    public void stopServer(){
        gui.stateNotHostingServer();
        assets.getServer().disconnect();
        assets.getServer().stop();
        if(assets.getClient() != null){
            gui.stateDisconnectedFromServer();
            assets.getClient().disconnect();
        }
        assets.setServer(null);
        assets.setClient(null);
    }
    
    public void refreshShipScreen(){
        ImageIcon scaledImage;
        System.out.println(assets.getSelected());
        if(assets.isOriented()){
            scaledImage = scaleImage(gui.getShipScreen(),
                    new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[assets.getSelected()] + "0" + ".png"));
        }
        else{
            scaledImage = scaleImage(gui.getShipScreen(),
                    new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[assets.getSelected()] + ".png"));
        }
        gui.getShipScreen().setIcon(scaledImage);
        gui.getGamePanel().repaint();
    }
    
    public void errorConnecting(){
        gui.addText("Could not connect to server");
    }
    
    public void chatMessageReceived(String message, String nickname){
        gui.addText("[" + nickname + "]" + ": " + message);
    }
    
    public void sendChatMessage(String message){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_MESSAGE, message, assets.getNickname()});
    }
    
    private void sendEndTurnMessage(){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_ENDTURN, assets.getTargetCoords()[0][0] + "," + assets.getTargetCoords()[0][1], String.valueOf(assets.getPlayerId())});
    }
    
    private void sendReadyMessage(){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_READY, GameConstants.MESSAGE_READY, assets.getNickname()});
    }
    
    private void sendHitMessage(String data){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_HIT, data, assets.getNickname()});
    }
    
    private void sendMissMessage(String data){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_MISS, data, assets.getNickname()});
    }
    
    private void sendSinkMessage(int shipIndex){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_SUNK, GameConstants.SHIP_NAMES[shipIndex], String.valueOf(assets.getPlayerId())});
    }
    
    private void sendGameOverMessage(){
        assets.getClient().sendData(new String[]{GameConstants.REQUEST_ALL_DESTROYED, "", String.valueOf(assets.getPlayerId())});
    }
    
    public void beginTurn(){
        assets.setTurn(true);
        gui.setEndTurn(true);
        chatMessageReceived(assets.getNickname() + " its your turn.", "SERVER");
    }
    
    public void processShot(String coords){
        String [] d = coords.split(",");
        int x = Integer.valueOf(d[0]);
        int y = Integer.valueOf(d[1]);
        
        if(checkHitAndDamage(x, y)){
            sendHitMessage(coords);
        }
        else{
            sendMissMessage(coords);
        }
    }
    
    public void refreshHits(byte type, String coords, String name){
        String [] d = coords.split(",");
        int x = Integer.valueOf(d[0]);
        int y = Integer.valueOf(d[1]);
        
        assets.setTileState(x, y, type);
        gui.getGamePanel().repaint();
    }
    
    private boolean checkHitAndDamage(int x, int y){
        byte tileHit;
        int thisX = 0;
        int thisY = 0;
        boolean hit = false;
        
        for(int i = 0; i < GameConstants.SHIPS.length; i++){
            tileHit = 0;
            for(int m = 0; m < GameConstants.SIZES[i]; m++){
                thisX = assets.getShipCoordinates()[i][0];
                thisY = assets.getShipCoordinates()[i][1];
                /* We check if the ship is oriented. */
                if(assets.getIsPlaced()[i][1]){
                    thisX += GameConstants.TILE_SIZE * m;
                } 
                else{
                    thisY += GameConstants.TILE_SIZE * m;
                }
                /*
                We also check if the other tiles of the ship have been hit. 
                */
                if(assets.getTileState(thisX, thisY) == GameConstants.TILE_STATE_HIT){
                    tileHit ++;
                }
                
                if(x == thisX && y == thisY){
                    hit = true;
                    tileHit++;
                }
            }
            if(hit){
                checkIfDestroyed(i, tileHit);
                return true;
            }
        }

        return false;
    }
    
    private void checkIfDestroyed(int shipIndex, byte damage){
        if(damage >= GameConstants.SIZES[shipIndex]){
            assets.setDestroyedShips(shipIndex, true);
            sendSinkMessage(shipIndex);
            if(isGameOver()){
                sendGameOverMessage();
            }
        }
    }
    
    /**
     * Checks if all of the players ships have been sunk.
     * @return 
     */
    private boolean isGameOver(){
        for(int i = 0; i < GameConstants.SHIPS.length; i ++){
            if(!assets.isShipDestroyed(i)){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Scales the given image to the given surface.
     * @param l
     * @param i
     * @return 
     */
    private ImageIcon scaleImage(JLabel l, ImageIcon i){
        ImageIcon imageToScale = i;
        BufferedImage b = new BufferedImage(imageToScale.getIconWidth(), imageToScale.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.createGraphics();
        g.drawImage(imageToScale.getImage(), 0, 0, null);
        g.dispose();
        imageToScale.setImage(b.getScaledInstance(l.getWidth(), l.getHeight(), BufferedImage.SCALE_SMOOTH));
        return imageToScale;
    }
    
    public void stateGameReady(){
        assets.setState(GameConstants.STATE_PLACING_BOATS);
        gui.setMessageLabel(assets.getNickname());
        gui.setChangeOrientation(true);
        assets.setGrid(true);
        assets.setSelected((byte)0);
        assets.resetShipVariables();
        ImageIcon scaledImage = scaleImage(gui.getShipScreen(), 
                new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[assets.getSelected()] + ".png"));
        gui.getShipScreen().setIcon(scaledImage);
        gui.drawGameMap();
    }
    
    public void stateGameBegun(){
        gui.setEndTurn(true);
        if(assets.getPlayerId() == 0){
            assets.setTurn(true);
        }
        else{
            assets.setTurn(false);
            gui.setEndTurn(false);
        }
        gui.addText("Player 1 starts first.");
        drawShipPlate();
        assets.setState(GameConstants.STATE_GAME);
    }
    
    public void statePostMatch(){
        assets.setTurn(false);
        assets.setState(GameConstants.STATE_POST_MATCH);
        gui.getEndTurnButton();
        assets.resetShipVariables();
    }
}
