package boatwars.main;

import boatwars.gui.MainGUI;
import boatwars.util.GameAssets;

public class BoatWars {
    private static GameAssets assets;
    
    public static void main(String[] args) {
        assets = new GameAssets();
        init(assets);
    }
    
    private static void init(GameAssets ass){
        new MainGUI(ass);
    }
    
}
