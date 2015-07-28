package boatwars.gui;

import boatwars.controller.MainController;
import boatwars.util.GameConstants;
import boatwars.util.GameAssets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class MainGUI extends javax.swing.JFrame implements KeyListener, ActionListener{
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private MouseListener mouse;
    
    private JButton changeOrientation;
    private javax.swing.JTextArea chatArea;
    private JTextField chatField;
    private JButton endTurnButton;
    private GamePanel gameScreen;
    private JLabel fieldLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private JLabel scorePicture;
    private javax.swing.JScrollPane scrollbar;
    private JLabel shipScreen;
    
    public MainGUI(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){}
        MainController.setGUI(this);
        mouse = new MouseListener();
        /*assets.getSoundControl().playSound(GameConstants.MUSIC_THEME);*/
        initComponents();
        initCustom();
        drawMenuScreen();
    }
    
    public void stateBoatSetupComplete(){
        changeOrientation.setEnabled(false);
        endTurnButton.setText("Ready!");
        endTurnButton.setEnabled(true);
    }

    public JButton getOrientationButton(){
        return changeOrientation;
    }
    
    public void setMessageLabel(String g){
        fieldLabel.setText(g + " says:");
    }
    
    public void addText(String g){
        chatArea.append("\n" + g);
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    public JTextField getChatField(){
        return this.chatField;
    }
    
    public JButton getEndTurnButton(){
        return this.endTurnButton;
    }
    
    public JLabel getShipScreen(){
        return this.shipScreen;
    }
    
    public JLabel getScorePicture(){
        return this.scorePicture;
    }
    
    public void setEndTurn(boolean b){
        this.endTurnButton.setEnabled(b);
    }
    
    public void setChangeOrientation(boolean b){
        this.changeOrientation.setEnabled(b);
    }

    public void stateCanDisconnectFromServer(){
        menuBar.getMenu(0).getItem(0).setEnabled(true);
        menuBar.getMenu(0).getItem(2).setEnabled(false);
    }
    
    public void stateDisconnectedFromServer(){
        menuBar.getMenu(0).getItem(0).setEnabled(false);
        menuBar.getMenu(0).getItem(2).setEnabled(true);
    }
    
    public void disableChat(){
        this.chatField.setEnabled(false);
    }
    
    private void drawMenuScreen(){
        this.chatField.setEnabled(false);
        MainController.drawPlateScreen();
        MainController.drawShipPlate();
    }
    
    public GamePanel getGamePanel(){
        return this.gameScreen;
    }
    
    public void drawGameMap(){
        this.chatField.setEnabled(true);
        gameScreen.repaint();
    }
    
    private void initButtons(){
        changeOrientation.addActionListener(this);
        changeOrientation.setActionCommand("orientation");
        endTurnButton.addActionListener(this);
        endTurnButton.setActionCommand("endturn");
    }
    
    private void initMenuBars(){
        menuBar = new JMenuBar();
        menu = new JMenu("Game");
        menuItem = new JMenuItem("Disconnect");
        menuItem.setActionCommand("disconnect");
        menuItem.addActionListener(this);
        menuItem.setEnabled(false);
        menu.add(menuItem);
        menu.addSeparator();
        
        JMenu m = new JMenu("New Game");
        menuItem = new JMenuItem("Join Game");
        menuItem.setActionCommand("joingame");
        menuItem.addActionListener(this);
        m.add(menuItem);
        menu.add(m);
        menu.addSeparator();
        menuItem = new JMenuItem("Quit Game");
        menuItem.setActionCommand("quitgame");
        menuItem.addActionListener(this);
        menu.add(menuItem);
       
        menuBar.add(menu);
        
        JButton specialButton = new JButton("About");
        specialButton.setActionCommand("about");
        specialButton.addActionListener(this);
        specialButton.setFocusable(false);
        menuBar.add(specialButton);
    }
    
    private void initCustom(){
        setTitle(GameConstants.TITLE +  " " + GameConstants.VERSION);
        initButtons();
        initMenuBars();
        setName("Main");
        setIconImage(new ImageIcon("").getImage());
        setJMenuBar(menuBar);
        addWindowListener(new WindowListener(){
            @Override
            public void windowOpened(WindowEvent e){}
            @Override
            public void windowClosing(WindowEvent e){
                if(e.getWindow().getName().equals("Main")){
                    MainController.actionQuitGame();
                }
            }
            @Override
            public void windowClosed(WindowEvent e){}
            @Override
            public void windowIconified(WindowEvent e){}
            @Override
            public void windowDeiconified(WindowEvent e){}
            @Override
            public void windowDeactivated(WindowEvent e){}
            @Override
            public void windowActivated(WindowEvent e){}
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        chatField.addKeyListener(this);
        gameScreen.addMouseListener(mouse);
        gameScreen.addMouseMotionListener(mouse);
        this.chatArea.setText(GameConstants.WELCOME);
        this.changeOrientation.setEnabled(false);
        this.endTurnButton.setEnabled(false);
    }
    
    @Override
    public void keyPressed(KeyEvent k) {}

    @Override
    public void keyReleased(KeyEvent k){
        MainController.keyPress(k);
    }

    @Override
    public void keyTyped(KeyEvent k) {}
    
    @Override
    public void actionPerformed(ActionEvent e){
        switch (e.getActionCommand()) {
            case "disconnect":
                MainController.disconnectFromServer();
                break;
            case "joingame":
                MainController.actionJoinGame();
                break;
            case "about":
                MainController.actionAbout();
                break;
            case "quitgame":
                MainController.actionQuitGame();
                break;
            case "orientation":
                MainController.actionOrientation();
                break;
            case "endturn":
                MainController.actionEndTurn();
                break;
        }
    }
    
    private class MouseListener extends MouseAdapter{
        
        @Override
        public void mouseMoved(MouseEvent e){
            if(GameAssets.getState() == GameConstants.STATE_GAME || GameAssets.getState() == GameConstants.STATE_PLACING_BOATS){
                int x = (e.getPoint().x / GameConstants.TILE_SIZE);
                int y = (e.getPoint().y  / GameConstants.TILE_SIZE);

                if(GameAssets.getMouseXY()[0] != x || GameAssets.getMouseXY()[1] != y){
                    GameAssets.setMouseXY(x, y);
                    MainController.refreshCursorGraphics();
                }
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e){
            if(GameAssets.getState() == GameConstants.STATE_PLACING_BOATS && !GameAssets.allPlaced()){
                MainController.actionPlaceBoat();
            }
            else if(GameAssets.getState() == GameConstants.STATE_GAME){
                MainController.actionPlaceTarget();
            }
        }
    }
    
    private void initComponents() {
        scrollbar = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        changeOrientation = new JButton();
        endTurnButton = new JButton();
        jPanel1 = new javax.swing.JPanel();
        shipScreen = new JLabel();
        jPanel2 = new javax.swing.JPanel();
        scorePicture = new JLabel();
        chatField = new JTextField();
        fieldLabel = new JLabel();
        gameScreen = new GamePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        scrollbar.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollbar.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        chatArea.setEditable(false);
        chatArea.setBackground(new java.awt.Color(51, 255, 51));
        chatArea.setColumns(20);
        chatArea.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        chatArea.setLineWrap(true);
        chatArea.setRows(5);
        chatArea.setWrapStyleWord(true);
        scrollbar.setViewportView(chatArea);

        changeOrientation.setText("Change orientation");

        endTurnButton.setBackground(new java.awt.Color(255, 51, 51));
        endTurnButton.setText("End Turn");

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shipScreen, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(shipScreen, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scorePicture, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scorePicture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        fieldLabel.setText("Player says:");

        javax.swing.GroupLayout gameScreenLayout = new javax.swing.GroupLayout(gameScreen);
        gameScreen.setLayout(gameScreenLayout);
        gameScreenLayout.setHorizontalGroup(
            gameScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        gameScreenLayout.setVerticalGroup(
            gameScreenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 240, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollbar)
                    .addComponent(chatField)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fieldLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(gameScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGap(90, 90, 90))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(endTurnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(changeOrientation, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gameScreen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollbar, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37))
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGap(18, 18, 18)
                .addComponent(changeOrientation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(endTurnButton)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setBounds(0, 0, 826, 742);
    }                                 
}
