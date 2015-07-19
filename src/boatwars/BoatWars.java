package boatwars;

import boatwars.gui.MainGUI;
import boatwars.util.GameAssets;

import java.io.File;

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
