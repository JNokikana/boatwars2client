package com.joonas.ninja.boatwarsgame;

import com.joonas.ninja.boatwarsgame.gui.MainGUI;
import com.joonas.ninja.boatwarsgame.util.GameAssets;

import java.io.File;

/**
 * BoatWars2.0 contains much shitty code from the previous 1.xx version but is redeemed by
 * changes from this new version.
 */
public class BoatWars {
    public static String PATH;

    public static void main(String[] args) {
        init();
    }
    
    private static void init(){
        try{
            PATH = new File("").getCanonicalPath();
        }catch(Exception e){}

        GameAssets.initialize();
        new MainGUI();
    }
    
}
