package com.joonas.ninja.boatwarsgame.util;

import com.joonas.ninja.boatwarsgame.net.Client;
import com.google.gson.Gson;

public class GameAssets {
    private static byte gameState;
    private static Client client;
    private static String nickname;
    private static boolean won;
    private static int playerId;
    private static boolean grid;
    private static byte selected;
    private static boolean oriented;
    /**
     * The origin coordinates of a ship.
     */
    private static int [][] shipCoordinates;
    private static boolean allPlaced;
    private static boolean targetPlaced;
    private static boolean isTurn;
    private static Gson gson;
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
        gson = new Gson();
        resetShipVariables();
    }
    
    public static void setGameWon(boolean b){
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
        targetPlaced = false;
        mouseXY = new int[2];
        grid = false;
        allPlaced = false;
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
        return hitSpaces[x / GameConstants.TILE_SIZE][y / GameConstants.TILE_SIZE];
    }
    
    public static byte[][] getHitSpaces(){
        return hitSpaces;
    }
    
    public static int[] getMouseXY(){
        return mouseXY;
    }
    
    public static boolean gameWon(){
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

    public synchronized static Gson getGson(){
        return gson;
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
    
    public static void setPlayerId(int id){
        playerId = id;
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
    
    public static Client getClient(){
        return client;
    }
    
    public static byte getState(){
        return gameState;
    }
    
    public static byte getSelectedShip(){
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
    
    public static int getPlayerId(){
        return playerId;
    }
}
