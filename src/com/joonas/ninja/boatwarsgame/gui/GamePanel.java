package com.joonas.ninja.boatwarsgame.gui;

import com.joonas.ninja.boatwarsgame.util.GameAssets;
import com.joonas.ninja.boatwarsgame.util.GameConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GamePanel extends JPanel{
    private String path;
    
    private void drawGraphics(Graphics2D g){
        try{
            path = new File("").getCanonicalPath();
        }catch(Exception e){}
        
        BufferedImage im = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics gf = im.getGraphics();
        
        if(GameAssets.getState() == GameConstants.STATE_MENU){
            gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_MENU).getImage(), 0, 0, null);
        }
        else if(GameAssets.getState() == GameConstants.STATE_POST_MATCH){
            if(GameAssets.gameWon()){
                gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_WON).getImage(), 0, 0, null);
            }
            else{
               gf.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_LOSE).getImage(), 0, 0, null); 
            }
        }
        else{
            drawGrid(gf);
            gf.setColor(Color.RED);
//            System.out.println(GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE + " " + GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE);
            gf.drawRect(GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE,
                    GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE, GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);

            positionBoats(gf);
            drawBoats(gf);
            drawTargetSpace(gf);
            drawTileState(gf);
        }
        g.drawImage(im, 0, 0, null);
    }
    
    private void drawTileState(Graphics g){
        if(GameAssets.getState() == GameConstants.STATE_GAME){
            for(int i = 0; i < GameAssets.getHitSpaces().length; i++){
                for(int m = 0; m < GameAssets.getHitSpaces()[0].length; m++){
                    if(GameAssets.getHitSpaces()[i][m] == GameConstants.TILE_STATE_HIT){
                        g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_HIT).getImage(),
                                i * GameConstants.TILE_SIZE, m * GameConstants.TILE_SIZE, null);
                    } 
                    else if(GameAssets.getHitSpaces()[i][m] == GameConstants.TILE_STATE_MISS){
                        g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_MISS).getImage(),
                                i * GameConstants.TILE_SIZE, m * GameConstants.TILE_SIZE, null);
                    }
                    else if(GameAssets.getHitSpaces()[i][m] == GameConstants.TILE_STATE_DESTROYED){
                        g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_DESTROYED).getImage(),
                                i * GameConstants.TILE_SIZE, m * GameConstants.TILE_SIZE, null);
                    }
                }
            }
        }
    }
    
    private void drawTargetSpace(Graphics g){
        if(GameAssets.isTargetPlaced()){
            g.drawImage(new ImageIcon(path + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_TARGET).getImage(),
                    GameAssets.getTargetCoords()[0][0], GameAssets.getTargetCoords()[0][1], null);
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
        if(GameAssets.getPlayerId() == 0){
            gf.fillRect(0, 0, 240, 240);
        }
        else{
            gf.fillRect(240, 0, 240, 240);
        }
    }
    
    private void positionBoats(Graphics gf){
        if(GameAssets.getState() == GameConstants.STATE_PLACING_BOATS && !GameAssets.allPlaced()){
            gf.setColor(new Color(0, 0, 0, (float)0.3));
            for(int i = 0; i < GameConstants.SIZES[GameAssets.getSelectedShip()]; i++){
                if(GameAssets.isOriented()){
                    gf.fillRect(GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE + (i * GameConstants.TILE_SIZE),
                            GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
                } 
                else{
                    gf.fillRect(GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE,
                            GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE + (i * GameConstants.TILE_SIZE),
                            GameConstants.TILE_SIZE, GameConstants.TILE_SIZE);
                }
            }
        }
    }
    
    private void drawBoats(Graphics gf){
        if(GameAssets.getState() == GameConstants.STATE_PLACING_BOATS || GameAssets.getState() == GameConstants.STATE_GAME){
            for(int i = 0; i < GameConstants.SHIPS.length; i++){
                if(GameAssets.getIsPlaced()[i][0]){
                    int x = GameAssets.getShipCoordinates()[i][0];
                    int y = GameAssets.getShipCoordinates()[i][1];

                    if(GameAssets.getIsPlaced()[i][1]){
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
