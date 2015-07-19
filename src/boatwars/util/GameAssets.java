package boatwars.util;

import boatwars.net.ConnectorClient;
import boatwars.net.Server;

public class GameAssets {
    private byte gameState;
    private Server server;
    private ConnectorClient client;
    private String nickname;
    private boolean won;
    private byte playerId;
    private boolean grid;
    private byte selected;
    private boolean oriented;
    private int [][] shipCoordinates;
    private boolean allPlaced;
    private boolean targetPlaced;
    private boolean isTurn;
    /* The coordinates of the tile that you wish to target. */
    private int [][] targetCoords;
    /* Tells whether the indicated space has been hit or not. The last value indicates whether
       the shot was a hit or not.*/
    private byte[][] hitSpaces;
    /** Indicates whether a ship is placed or not and 
     * if it is oriented.
     */
    private boolean [][] isPlaced;
    private int[] mouseXY;
    private boolean [] destroyedShips;
    private SoundControl soundControl;
    
    public GameAssets(){
        gameState = GameConstants.STATE_MENU;
        soundControl = new SoundControl();
        grid = false;
        oriented = false;
        resetShipVariables();
    }
    
    public void setGameResult(boolean b){
        won = b;
    }
    
    public void resetShipVariables(){
        isPlaced = new boolean[GameConstants.SHIPS.length][2];
        shipCoordinates = new int[GameConstants.SHIPS.length][2];
        this.targetCoords = new int[1][2];
        targetCoords[0][0] = -1;
        this.hitSpaces = new byte[20][10];
        destroyedShips = new boolean[GameConstants.SHIPS.length];
        isTurn = false;
        this.mouseXY = new int[2];
        grid = false;
    }
    
    public void setDestroyedShips(int i, boolean b){
        destroyedShips[i] = b;
    }
    
    public void setIsPlaced(int i, boolean placed, boolean oriented){
        this.isPlaced[i][0] = placed;
        this.isPlaced[i][1] = oriented;
    }
    
    public void setShipCoordinates(int x, int y, int i){
        this.shipCoordinates[i][0] = x;
        this.shipCoordinates[i][1] = y;
    }
    
    public void setTargetPlaced(boolean b){
        this.targetPlaced = b;
    }
    
    public void setTileState(int x, int y, byte value){
        this.hitSpaces[x / GameConstants.TILE_SIZE][y / GameConstants.TILE_SIZE] = value;
    }
    
    public boolean[][] getIsPlaced(){
        return this.isPlaced;
    }
    
    public byte getTileState(int x, int y){
        System.out.println(x + " " + y);
        return this.hitSpaces[x / GameConstants.TILE_SIZE][y / GameConstants.TILE_SIZE];
    }
    
    public byte[][] getHitSpaces(){
        return this.hitSpaces;
    }
    
    public int[] getMouseXY(){
        return this.mouseXY;
    }
    
    public SoundControl getSoundControl(){
        return this.soundControl;
    }
    
    public boolean gameWon(){
        return this.won;
    }
    
    public void setTurn(boolean b){
        this.isTurn = b;
    }
    
    public boolean isTurn(){
        return this.isTurn;
    }
    
    public boolean isShipDestroyed(int i){
        return destroyedShips[i];
    }
    
    public void setMouseXY(int x, int y){
        this.mouseXY[0] = x;
        this.mouseXY[1] = y;
    }
    
    public int[][] getShipCoordinates(){
        return this.shipCoordinates;
    }
    
    public void setSelected(byte b){
        this.selected = b;
    }
    
    public void incSelected(){
        selected ++;
        if(selected == GameConstants.SHIPS.length){
            allPlaced = true;
        }
    }
    
    public boolean allPlaced(){
        return allPlaced;
    }
    
    public boolean isGrid(){
        return this.grid;
    }
    
    public boolean isOriented(){
        return this.oriented;
    }
    
    public void switchOrientation(){
        this.oriented =! oriented;
    }
    
    public void setGrid(boolean b){
        this.grid = b;
    }
    
    public boolean isHost(){
        return this.server != null;
    }
    
    public void setPlayerId(byte playerId){
        this.playerId = playerId;
    }
    
    public void setServer(Server server){
        this.server = server;
    }
    
    public void setClient(ConnectorClient client){
        this.client = client;
    }
    
    public void setState(byte g){
        this.gameState = g;
    }
    
    public void setNickname(String nick){
        this.nickname = nick;
    }
    
    public Server getServer(){
        return this.server;
    }
    
    public ConnectorClient getClient(){
        return this.client;
    }
    
    public byte getState(){
        return this.gameState;
    }
    
    public byte getSelected(){
        return this.selected;
    }
    
    public int[][] getTargetCoords(){
        return this.targetCoords;
    }
    
    public void setTargetCoords(int x, int y){
        this.targetCoords[0][0] = x;
        this.targetCoords[0][1] = y;
    }
    
    public boolean isTargetPlaced(){
        return this.targetPlaced;
    }
    
    public String getNickname(){
        return this.nickname;
    }
    
    public byte getPlayerId(){
        return this.playerId;
    }
}
