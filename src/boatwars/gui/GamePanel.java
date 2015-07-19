package boatwars.gui;

import boatwars.util.GameAssets;
import boatwars.util.GameConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel{
    private String path;
    private GameAssets assets;
    
    public GamePanel(GameAssets assets){
        this.assets = assets;
    }
    
    private void drawGraphics(Graphics2D g){
        try{
            path = new File("").getCanonicalPath();
        }catch(Exception e){}
        
        BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics gf = im.getGraphics();
        
        if(assets.getState() == GameConstants.STATE_MENU){
            gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_MENU).getImage(), 0, 0, null);
        }
        else if(assets.getState() == GameConstants.STATE_POST_MATCH){
            if(assets.gameWon()){
                gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_WON).getImage(), 0, 0, null);
            }
            else{
               gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_LOSE).getImage(), 0, 0, null); 
            }
        }
        else{
            drawGrid(gf);
            gf.setColor(Color.RED);
            gf.drawRect(assets.getMouseXY()[0] * GameConstants.TILE_SIZE, 
                    assets.getMouseXY()[1] * GameConstants.TILE_SIZE, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

            positionBoats(gf);
            drawBoats(gf);
            drawTargetSpace(gf);
            drawTileState(gf);
        }
        g.drawImage(im, 0, 0, null);
    }
    
    private void drawTileState(Graphics g){
        if(assets.getState() == GameConstants.STATE_GAME){
            for(int i = 0; i < assets.getHitSpaces().length; i++){
                for(int m = 0; m < assets.getHitSpaces()[0].length; m++){
                    if(assets.getHitSpaces()[i][m] == GameConstants.TILE_STATE_HIT){
                        g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_HIT).getImage(),
                                i * GameConstants.TILE_SIZE, m * GameConstants.TILE_SIZE, null);
                    } 
                    else if(assets.getHitSpaces()[i][m] == GameConstants.TILE_STATE_MISS){
                        g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_MISS).getImage(),
                                i * GameConstants.TILE_SIZE, m * GameConstants.TILE_SIZE, null);
                    }
                }
            }
        }
    }
    
    private void drawTargetSpace(Graphics g){
        if(assets.isTargetPlaced()){
            g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_TARGET).getImage(), 
                    assets.getTargetCoords()[0][0], assets.getTargetCoords()[0][1], null);
        }
    }
    
    private void drawGrid(Graphics gf){
        gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_MAP).getImage(), 0, 0, null);
        gf.setColor(Color.GREEN);
        for(int y = 0; y < this.getHeight(); y += GameConstants.TILE_SIZE){
            gf.drawLine(0, y, this.getWidth(), y);
        }
        for(int x = 0; x < this.getWidth(); x += GameConstants.TILE_SIZE){
            gf.drawLine(x, 0, x, this.getHeight());
        }
        gf.setColor(new Color(0, 0, 0, (float)0.4));
        if(assets.getPlayerId() == 0){
            gf.fillRect(0, 0, 240, 240);
        }
        else{
            gf.fillRect(240, 0, 240, 240);
        }
    }
    
    private void positionBoats(Graphics gf){
        if(assets.getState() == GameConstants.STATE_PLACING_BOATS && !assets.allPlaced()){
            gf.setColor(new Color(0, 0, 0, (float)0.3));
            for(int i = 0; i < GameConstants.SIZES[assets.getSelected()]; i++){
                if(assets.isOriented()){
                    gf.fillRect(assets.getMouseXY()[0] * GameConstants.TILE_SIZE + (i * GameConstants.TILE_SIZE),
                            assets.getMouseXY()[1] * GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
                } 
                else{
                    gf.fillRect(assets.getMouseXY()[0] * GameConstants.TILE_SIZE,
                            assets.getMouseXY()[1] * GameConstants.TILE_SIZE + (i * GameConstants.TILE_SIZE),
                            GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
                }
            }
        }
    }
    
    private void drawBoats(Graphics gf){
        if(assets.getState() == GameConstants.STATE_PLACING_BOATS || assets.getState() == GameConstants.STATE_GAME){
            for(int i = 0; i < GameConstants.SHIPS.length; i++){
                if(assets.getIsPlaced()[i][0]){
                    int x = assets.getShipCoordinates()[i][0];
                    int y = assets.getShipCoordinates()[i][1];

                    if(assets.getIsPlaced()[i][1]){
                        gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[i]
                                + "0" + ".png").getImage(), x, y, null);
                    } 
                    else{
                        gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[i]
                                + ".png").getImage(), x, y, null);
                    }
                }
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        drawGraphics((Graphics2D)g);
    }
}
