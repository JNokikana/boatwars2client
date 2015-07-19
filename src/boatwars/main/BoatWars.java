package boatwars.main;

import boatwars.gui.MainGUI;
import boatwars.util.GameAssets;

import java.io.File;

public class BoatWars {
    public static String PATH;

    public static void main(String[] args) {
        try{
            PATH = new File("").getCanonicalPath();
        }catch(Exception e){}
        init();
    }
    
    private static void init(){
        GameAssets.initialize();
        new MainGUI();
    }
    
}
