package boatwars.util;

import boatwars.net.Client;
import boatwars.net.Server;

public class GameAssets {
    private static byte gameState;
    private static Server server;
    private static Client client;
    private static String nickname;
    private static boolean won;
    private static byte playerId;
    private static boolean grid;
    private static byte selected;
    private static boolean oriented;
    private static int [][] shipCoordinates;
    private static boolean allPlaced;
    private static boolean targetPlaced;
    private static boolean isTurn;
    /* The coordinates of the tile that you wish to target. */
    private static int [][] targetCoords;
    /* Tells whether the indicated space has been hit or not. The last value indicates whether
       the shot was a hit or not.*/
    private static byte[][] hitSpaces;
    /** Indicates whether a ship is placed or not and 
     * if it is oriented.
     */
    private static boolean [][] isPlaced;
    private static int[] mouseXY;
    private static boolean [] destroyedShips;

    public static void initialize(){
        gameState = GameConstants.STATE_MENU;
        grid = false;
        oriented = false;
        resetShipVariables();
    }
    
    public static void setGameResult(boolean b){
        won = b;
    }
    
    public static void resetShipVariables(){
        isPlaced = new boolean[GameConstants.SHIPS.length][2];
        shipCoordinates = new int[GameConstants.SHIPS.length][2];
        targetCoords = new int[1][2];
        targetCoords[0][0] = -1;
        hitSpaces = new byte[20][10];
        destroyedShips = new boolean[GameConstants.SHIPS.length];
        isTurn = false;
        mouseXY = new int[2];
        grid = false;
    }
    
    public static void setDestroyedShips(int i, boolean b){
        destroyedShips[i] = b;
    }
    
    public static void setIsPlaced(int i, boolean placed, boolean oriented){
        isPlaced[i][0] = placed;
        isPlaced[i][1] = oriented;
    }
    
    public static void setShipCoordinates(int x, int y, int i){
        shipCoordinates[i][0] = x;
        shipCoordinates[i][1] = y;
    }
    
    public static void setTargetPlaced(boolean b){
        targetPlaced = b;
    }
    
    public static void setTileState(int x, int y, byte value){
        hitSpaces[x / GameConstants.TILE_SIZE][y / GameConstants.TILE_SIZE] = value;
    }
    
    public static boolean[][] getIsPlaced(){
        return isPlaced;
    }
    
    public static byte getTileState(int x, int y){
        System.out.println(x + " " + y);
        return hitSpaces[x / GameConstants.TILE_SIZE][y / GameConstants.TILE_SIZE];
    }
    
    public byte[][] getHitSpaces(){
        return hitSpaces;
    }
    
    public static int[] getMouseXY(){
        return mouseXY;
    }
    
    public boolean gameWon(){
        return won;
    }
    
    public static void setTurn(boolean b){
        isTurn = b;
    }
    
    public static boolean isTurn(){
        return isTurn;
    }
    
    public static boolean isShipDestroyed(int i){
        return destroyedShips[i];
    }
    
    public static void setMouseXY(int x, int y){
        mouseXY[0] = x;
        mouseXY[1] = y;
    }
    
    public static int[][] getShipCoordinates(){
        return shipCoordinates;
    }
    
    public static void setSelected(byte b){
        selected = b;
    }
    
    public static void incSelected(){
        selected ++;
        if(selected == GameConstants.SHIPS.length){
            allPlaced = true;
        }
    }
    
    public static boolean allPlaced(){
        return allPlaced;
    }
    
    public boolean isGrid(){
        return grid;
    }
    
    public static boolean isOriented(){
        return oriented;
    }
    
    public static void switchOrientation(){
        oriented =! oriented;
    }
    
    public static void setGrid(boolean b){
        grid = b;
    }
    
    public boolean isHost(){
        return server != null;
    }
    
    public static void setPlayerId(byte id){
        playerId = id;
    }
    
    public void setServer(Server server){
        server = server;
    }
    
    public void setClient(Client client){
        client = client;
    }
    
    public static void setState(byte g){
        gameState = g;
    }
    
    public static void setNickname(String nick){
        nickname = nick;
    }
    
    public Server getServer(){
        return server;
    }
    
    public static Client getClient(){
        return client;
    }
    
    public static byte getState(){
        return gameState;
    }
    
    public static byte getSelected(){
        return selected;
    }
    
    public static int[][] getTargetCoords(){
        return targetCoords;
    }
    
    public static void setTargetCoords(int x, int y){
        targetCoords[0][0] = x;
        targetCoords[0][1] = y;
    }
    
    public static boolean isTargetPlaced(){
        return targetPlaced;
    }
    
    public static String getNickname(){
        return nickname;
    }
    
    public static byte getPlayerId(){
        return playerId;
    }
}
