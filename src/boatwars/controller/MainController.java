package boatwars.controller;

import boatwars.BoatWars;
import boatwars.gui.MainGUI;
import boatwars.gui.NewGameJoinGUI;
import boatwars.net.Client;
import boatwars.net.MessageObject;
import boatwars.util.GameAssets;
import boatwars.util.GameConstants;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MainController {
    private static MainGUI gui;

    public static void setGUI(MainGUI g) {
        gui = g;
    }

    public static void drawPlateScreen() {
        ImageIcon scaledImage = scaleImage(gui.getScorePicture(),
                new ImageIcon(BoatWars.PATH + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_PLATE));
        gui.getScorePicture().setIcon(scaledImage);
    }

    public static void drawShipPlate() {
        ImageIcon scaledImage = scaleImage(gui.getShipScreen(),
                new ImageIcon(BoatWars.PATH + GameConstants.PATH_GRAPHICS + GameConstants.GRAPHICS_PLATE));
        gui.getShipScreen().setIcon(scaledImage);
    }

    public static void sunkShip(MessageObject data){
        if(!String.valueOf(GameAssets.getPlayerId()).equals(data.getSender())){
            gui.addText("You sunk the enemy " + data.getMessage() + "!");
        }
        else{
            gui.addText("Your " + data.getMessage() + " has been sunk!");
        }
    }

    public static void keyPress(KeyEvent k) {
        if ((GameAssets.getState() != GameConstants.STATE_MENU) &&
                k.getKeyCode() == KeyEvent.VK_ENTER
                && gui.getChatField().getText().length() > 0 && gui.getChatField().hasFocus()) {
            Client.sendChatMessage(gui.getChatField().getText());
            gui.getChatField().setText(null);
        }
    }

    private static void showServerNotice() {
        JOptionPane.showMessageDialog(null, "To play online you must open port " + GameConstants.PORT + " "
                + " from your router settings.", "Start Server", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void actionOrientation() {
        GameAssets.switchOrientation();
        refreshShipScreen();
    }

    public static void actionJoinGame() {
        new NewGameJoinGUI();
    }

    public static void actionQuitGame() {
        int selected = JOptionPane.showConfirmDialog(null,
                "Are you sure you would like to quit?", "Quit Game", JOptionPane.YES_NO_OPTION);
        if (selected == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void actionAbout() {
        JOptionPane.showMessageDialog(null, GameConstants.ABOUT, GameConstants.TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void actionPlaceBoat() {
        if (isValidSpot()) {
            GameAssets.setIsPlaced(GameAssets.getSelectedShip(), true, GameAssets.isOriented());
            GameAssets.setShipCoordinates(GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE,
                    GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE, GameAssets.getSelectedShip());
            GameAssets.incSelected();
            if (GameAssets.allPlaced()) {
                boatSetupComplete();
            } else {
                refreshShipScreen();
            }

            gui.getGamePanel().repaint();
        } else {
            errorCannotPlaceShipThere();
        }
    }

    public static void actionEndTurn() {
        if (gui.getEndTurnButton().getText().equals("Ready!")) {
            GameAssets.setState(GameConstants.STATE_WAITING);
            gui.setEndTurn(false);
            Client.sendReadyMessage();
            gui.getEndTurnButton().setText("End Turn");
        } else {
            if (GameAssets.isTargetPlaced()) {
                gui.addText("Thus ends your turn!");
                GameAssets.setTargetPlaced(false);
                GameAssets.setTurn(false);
                gui.setEndTurn(false);
                Client.sendTargetMessage(GameAssets.getTargetCoords()[0][0], GameAssets.getTargetCoords()[0][1]);
            } else {
                errorNoTarget();
            }
        }
    }

    public static void actionPlaceTarget() {
        if (GameAssets.isTurn() && GameAssets.getState() == GameConstants.STATE_GAME) {
            int x = GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE;
            int y = GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE;

            if (isValidTargetSpot(x, y)) {
                if (GameAssets.getTargetCoords()[0][0] != x || GameAssets.getTargetCoords()[0][1] != y) {
                    GameAssets.setTargetPlaced(true);
                    GameAssets.setTargetCoords(x, y);
                    gui.getGamePanel().repaint();
                } else {
                    actionEndTurn();
                }
            } else {
                errorCannotTargetThere();
            }
        }
    }

    /**
     * We check if the targeted tile is free or not and whether it is in the
     * correct area.
     *
     * @param x
     * @param y
     * @return
     */
    private static boolean isValidTargetSpot(int x, int y) {
        if (GameAssets.getTileState(x, y) != GameConstants.TILE_STATE_EMPTY) {
            return false;
        }

        if (GameAssets.getPlayerId() == 0) {
            return x < 240;
        } else {
            return x >= 240;
        }
    }

    /**
     * Gives the coordinates of the tiles the ship to be placed would occupy.
     *
     * @param mouseX
     * @param mouseY
     * @return
     */
    private static int[][] getSelectedShipCoordinates(int mouseX, int mouseY) {
        int[][] shipCoordinates;
        shipCoordinates = new int[GameConstants.SIZES[GameAssets.getSelectedShip()]][2];
        for (int i = 0; i < shipCoordinates.length; i++) {
            if (GameAssets.isOriented()) {
                shipCoordinates[i][0] = mouseX + (GameConstants.TILE_SIZE * i);
                shipCoordinates[i][1] = mouseY;
            } else {
                shipCoordinates[i][0] = mouseX;
                shipCoordinates[i][1] = mouseY + (GameConstants.TILE_SIZE * i);
            }
        }

        return shipCoordinates;
    }

    /**
     * Checks whether the ship to be placed collides with other ships.
     *
     * @param coords
     * @return
     */
    private static boolean shipIsCollisionFree(int[][] coords) {
        for (int i = 0; i < GameConstants.SHIPS.length; i++) {

            if (GameAssets.getIsPlaced()[i][0]) {
                for (int m = 0; m < GameConstants.SIZES[i]; m++) {
                    int thisX = GameAssets.getShipCoordinates()[i][0];
                    int thisY = GameAssets.getShipCoordinates()[i][1];
                    /* We check if the ship is oriented. */
                    if (GameAssets.getIsPlaced()[i][1]) {
                        thisX += GameConstants.TILE_SIZE * m;
                    } else {
                        thisY += GameConstants.TILE_SIZE * m;
                    }

                    for (int r = 0; r < coords.length; r++) {
                        if (coords[r][0] == thisX && coords[r][1] == thisY) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private static boolean shipIsOnValidZone(int[][] coords) {
        for (int i = 0; i < coords.length; i++) {
            int shipX = coords[i][0];
            int shipY = coords[i][1];

            if (shipY > GameConstants.FIELD_Y_MAX ||
                    shipX > GameConstants.FIELD_X_MAX ||
                    shipX < 0 ||
                    shipY < 0) {
                return false;
            }

            if ((GameAssets.getPlayerId() == 0 &&
                    shipX < GameConstants.BORDER_PLAYER_ONE) || (GameAssets.getPlayerId() == 1 &&
                    shipX > GameConstants.BORDER_PLAYER_TWO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether this spot is valid for ship placement.
     *
     * @return
     */
    private static boolean isValidSpot() {
        int mouseX = GameAssets.getMouseXY()[0] * GameConstants.TILE_SIZE;
        int mouseY = GameAssets.getMouseXY()[1] * GameConstants.TILE_SIZE;

        int[][] shipCoordinates = getSelectedShipCoordinates(mouseX, mouseY);

        if ((GameAssets.getPlayerId() == 0 &&
                mouseX < GameConstants.BORDER_PLAYER_ONE) || (GameAssets.getPlayerId() == 1 &&
                mouseX > GameConstants.BORDER_PLAYER_TWO)) {
            return false;
        }

        return shipIsCollisionFree(shipCoordinates) && shipIsOnValidZone(shipCoordinates);
    }

    private static void errorCannotPlaceShipThere() {
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_CANNOT_PLACE_SHIP, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void errorCannotTargetThere() {
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_CANNOT_TARGET_THERE, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void errorNoTarget() {
        JOptionPane.showMessageDialog(null, GameConstants.ERROR_NO_TARGET, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void boatSetupComplete() {
        gui.stateBoatSetupComplete();
        gui.addText("Boats placed. When ready press 'Ready!'");
    }

    public static void attemptConnection(String ip) {
        gui.addText("Establishing connection...");
        if (Client.connectToServer(ip)) {
            gui.stateCanDisconnectFromServer();
            gui.addText("Connected to server at " + ip);
        } else {
            gui.addText("Could not connect to server at " + ip);
        }
    }

    public static void refreshCursorGraphics() {
        gui.getGamePanel().repaint();
    }

    public static void setName(String g) {
        GameAssets.setNickname(g);
    }

    public static void disconnectFromServer() {
        MainController.showMessage("Disconnecting..", "CLIENT");
        try {
            Client.disconnectFromServer();
        } catch (Exception e) {
        }

        gui.stateDisconnectedFromServer();
        gui.disableChat();
    }

    public static void refreshShipScreen() {
        ImageIcon scaledImage;
        if (GameAssets.isOriented()) {
            scaledImage = scaleImage(gui.getShipScreen(),
                    new ImageIcon(BoatWars.PATH + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[GameAssets.getSelectedShip()] + "0" + ".png"));
        } else {
            scaledImage = scaleImage(gui.getShipScreen(),
                    new ImageIcon(BoatWars.PATH + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[GameAssets.getSelectedShip()] + ".png"));
        }
        gui.getShipScreen().setIcon(scaledImage);
        gui.getGamePanel().repaint();
    }

    public void errorConnecting() {
        gui.addText("Could not connect to server");
    }

    public synchronized static void showMessage(String message, String source) {
        gui.addText("[" + source + "]" + ": " + message);
    }

    private static void sendGameOverMessage() {
//        GameAssets.getClient().sendData(new String[]{GameConstants.REQUEST_ALL_DESTROYED, "", String.valueOf(GameAssets.getPlayerId())});
    }

    public static void checkBeginTurnProcessShot(MessageObject data) {
        int thisId = Integer.valueOf(data.getMessage());
        if (thisId != GameAssets.getPlayerId()) {
            /* The end turn message also contains the coordinates where the opponent shot. */
            processShot(data.getX(), data.getY());
            GameAssets.setTurn(true);
            gui.setEndTurn(true);
            showMessage(GameAssets.getNickname() + " its your turn.", GameConstants.CLIENT_NAME);
        }
    }

    public static void processShot(int x, int y) {
        if (checkHitAndDamage(x, y)) {
            System.out.println("Osu");
            Client.sendHitMessage(x, y);
        } else {
            Client.sendMissMessage(x, y);
        }
    }

    public static void refreshHits(byte type, int x, int y) {
        GameAssets.setTileState(x, y, type);
        gui.getGamePanel().repaint();
    }

    private static boolean checkHitAndDamage(int x, int y) {
        byte tileHit;
        int thisX = 0;
        int thisY = 0;
        boolean hit = false;

        for (int i = 0; i < GameConstants.SHIPS.length; i++) {
            tileHit = 0;
            for (int m = 0; m < GameConstants.SIZES[i]; m++) {
                thisX = GameAssets.getShipCoordinates()[i][0];
                thisY = GameAssets.getShipCoordinates()[i][1];
                /* We check if the ship is oriented. */
                if (GameAssets.getIsPlaced()[i][1]) {
                    thisX += GameConstants.TILE_SIZE * m;
                } else {
                    thisY += GameConstants.TILE_SIZE * m;
                }
                /*
                We also check if the other tiles of the ship have been hit. 
                */
                if (GameAssets.getTileState(thisX, thisY) == GameConstants.TILE_STATE_HIT) {
                    tileHit++;
                }

                if (x == thisX && y == thisY) {
                    hit = true;
                    tileHit++;
                }
            }
            if (hit) {
                checkIfDestroyed(i, tileHit);
                return true;
            }
        }

        return false;
    }

    private static void checkIfDestroyed(int shipIndex, byte damage) {
        if (damage >= GameConstants.SIZES[shipIndex]) {
            GameAssets.setDestroyedShips(shipIndex, true);
            Client.sendSinkMessage(shipIndex);
            if (isGameOver()) {
                sendGameOverMessage();
            }
        }
    }

    /**
     * Checks if all of the players ships have been sunk.
     *
     * @return
     */
    private static boolean isGameOver() {
        for (int i = 0; i < GameConstants.SHIPS.length; i++) {
            if (!GameAssets.isShipDestroyed(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scales the given image to the given surface.
     *
     * @param l
     * @param i
     * @return
     */
    private static ImageIcon scaleImage(JLabel l, ImageIcon i) {
        ImageIcon imageToScale = i;
        BufferedImage b = new BufferedImage(imageToScale.getIconWidth(), imageToScale.getIconHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = b.createGraphics();
        g.drawImage(imageToScale.getImage(), 0, 0, null);
        g.dispose();
        imageToScale.setImage(b.getScaledInstance(l.getWidth(), l.getHeight(), BufferedImage.SCALE_SMOOTH));
        return imageToScale;
    }

    public static void stateGameBegin(String id) {
        GameAssets.setPlayerId(Integer.valueOf(id));
        gui.addText("You are player " + (GameAssets.getPlayerId() + 1) + ".");
        gui.addText(GameConstants.INFO_MESSAGE_BEGIN);
        GameAssets.setState(GameConstants.STATE_PLACING_BOATS);
        gui.setMessageLabel(GameAssets.getNickname());
        gui.setChangeOrientation(true);
        GameAssets.setGrid(true);
        GameAssets.setSelected((byte) 0);
        GameAssets.resetShipVariables();
        ImageIcon scaledImage = scaleImage(gui.getShipScreen(),
                new ImageIcon(BoatWars.PATH + GameConstants.PATH_GRAPHICS + GameConstants.SHIPS[GameAssets.getSelectedShip()] + ".png"));
        gui.getShipScreen().setIcon(scaledImage);
        gui.drawGameMap();
    }

    public static void stateGameRunning() {
        gui.setEndTurn(true);
        if (GameAssets.getPlayerId() == 0) {
            GameAssets.setTurn(true);
        } else {
            GameAssets.setTurn(false);
            gui.setEndTurn(false);
        }
        gui.addText("Player 1 starts first.");
        drawShipPlate();
        GameAssets.setState(GameConstants.STATE_GAME);
    }

    public static void statePostMatch() {
        GameAssets.setTurn(false);
        GameAssets.setState(GameConstants.STATE_POST_MATCH);
        gui.getEndTurnButton();
        GameAssets.resetShipVariables();
    }
}
