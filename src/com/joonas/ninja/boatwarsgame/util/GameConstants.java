package com.joonas.ninja.boatwarsgame.util;

import java.io.File;

public class GameConstants {
    public final static String TITLE = "BoatWars";
    public final static String VERSION = "2.0";
    public final static int MAX_PLAYERS = 2;
    public final static byte STATE_MENU = 0;
    public final static byte STATE_PLACING_BOATS = 1;
    public final static byte STATE_WAITING = 2;
    public final static byte STATE_GAME = 3;
    public final static byte STATE_POST_MATCH = 4;

    public static final int BORDER_PLAYER_ONE = 240;
    public static final int BORDER_PLAYER_TWO = 216;

    public static final int FIELD_Y_MAX = 216;
    public static final int FIELD_X_MAX = 456;

    public final static String INFO_MESSAGE_BEGIN = "Both players are present. Place your boats.";

    public static final String CLIENT_NAME = "CLIENT";
    public static final String SERVER_NAME = "SERVER";

    public final static int PORT = 17413;
    /*testi*/
    public final static String REQUEST_JOIN = "JOIN";
    public final static String REQUEST_MESSAGE = "MESSAGE";
    public final static String REQUEST_SERVER = "SERVER";
    public final static String REQUEST_PLAYER = "PLAYER";
    public final static String REQUEST_DISCONNECT = "DISCONNECT";
    public final static String REQUEST_CLIENT_BYE = "IT IS A GOOD DAY TO DIE!";
    public final static String REQUEST_READY = "READY";
    public final static String REQUEST_ENDTURN = "ENDTURN";
    public final static String REQUEST_BEGIN = "BEGIN";
    public final static String REQUEST_HIT = "HIT";
    public final static String REQUEST_MISS = "MISS";
    public final static String REQUEST_ALL_DESTROYED = "YOU SUNK MY BATTLESHIPS!";
    public final static String REQUEST_SUNK = "SUNK";
    public final static String REQUEST_INFO = "INFO";
    public final static String REQUEST_GAMEPLAY_START = "GAMEON";
    public final static String REQUEST_REMATCH_YES = "REMATCH_YES";
    public final static String REQUEST_REMATCH_NO = "REMATCH_NO";
    public final static String REQUEST_RESET_GAME = "RESET";

    public static final String INFO_DISCONNECTED = "Disconnected from server at ";
    
    public final static byte TILE_STATE_EMPTY = 0;
    public final static byte TILE_STATE_MISS = 1;
    public final static byte TILE_STATE_HIT = 2;
    public final static byte TILE_STATE_DESTROYED = 3;
    
    public final static String ABOUT = "This game was made in 2014 by Joonas Nousiainen. Making it was very fun.";
    
    public final static int TIMEOUT = 4000;
    
    public final static String PATH_GRAPHICS = File.separator + "Data" + File.separator + "Graphics" + File.separator;
    
    public final static String MUSIC_THEME = "theme.wav";
    
    public final static String SOUND_SUNK = "explosion.wav";
    public final static String SOUND_HIT = "hit.wav";
    public final static String SOUND_HIT_SHOT = "hit_shot.wav";
    public final static String SOUND_MISS_SHOT = "miss_shot.wav";
    
    public final static String GRAPHICS_MENU = "title.png";
    public final static String GRAPHICS_MAP = "water.png";
    public final static String GRAPHICS_TARGET = "target.png";
    public final static String GRAPHICS_HIT = "hit.png";
    public final static String GRAPHICS_MISS = "miss.png";
    public final static String GRAPHICS_WON = "endWin.png";
    public final static String GRAPHICS_LOSE = "endLose.png";
    public final static String GRAPHICS_PLATE = "plate.png";
    public final static String GRAPHICS_DESTROYED = "explosion.png";
    
    public final static String MESSAGE_READY = " is ready.";
    
    public final static byte TILE_SIZE = 24;
    
    public final static String DEFAULT_NAME = "Player";
    
    public final static String SERVER_CONNECTION_ON = "Session started.";
    
    public final static String PNG = ".png";
    
    public final static String CARRIER = "carrier";
    public final static String BATTLESHIP = "battleship";
    public final static String CRUISER = "cruiser";
    public final static String SUBMARINE = "submarine";
    public final static String DESTROYER = "destroyer";
    
    public final static String ERROR_CANNOT_PLACE_SHIP = "You cannot place a ship there!";
    public final static String ERROR_NO_TARGET = "You have not selected a target space.";
    public final static String ERROR_ALREADY_TARGETED = "You have already targeted that spot.";
    public final static String ERROR_CANNOT_TARGET_THERE = "You cannot target that area.";
    
    public final static String[] SHIPS = {DESTROYER, SUBMARINE, CRUISER, BATTLESHIP, CARRIER};
    public final static byte[] SIZES = {2,3,3,4,5};
    public final static String[] SHIP_NAMES = {"Destroyer", "Submarine", "Cruiser", "Battleship", "Carrier"};
    
    public final static String WELCOME = "Welcome to BoatWars! Select"
            + " 'Join Game' from the 'New Game'-menu to start playing!\n-----------------------------"
            + "------------------------------";
}
