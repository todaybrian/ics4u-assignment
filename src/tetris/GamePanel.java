package tetris;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import tetris.controls.KeyboardInput;
import tetris.controls.MouseInput;
import tetris.game.Tetris;
import tetris.gui.GameBackground;
import tetris.gui.Gui;
import tetris.gui.GuiWelcome;
import tetris.music.MusicPlayer;
import tetris.settings.GameSettings;
import tetris.util.Assets;


public class GamePanel extends JPanel implements Runnable {
    //Store GamePanel instance
    //This is a technique used in games like Minecraft
    private static GamePanel instance;

    //internal height and width used for rendering
    //We pretend to draw stuff on a 1920 by 1080 screen. This gets scaled by GraphicsWrapper
    public static final int INTERNAL_WIDTH = 1920;
    public static final int INTERNAL_HEIGHT = 1080;

    //Width and height of the game on the current screen
    public int gameWidth;
    public int gameHeight;

    public int renderHeight;

    //Vertical/Horizontal padding of the game, used in case monitor is not 16:9
    public int verticalPadding;
    public int horizontalPadding;

    public Thread gameThread;
    public Image image;

    //# of nanoseconds between each render/physics update frame
    private double renderNS;
    private double physicsNS;

    //Displayed menu
    private Gui gui;

    //Keyboard Input class
    private KeyboardInput keyboardInput;

    private GameSettings gameSettings;

    private GameBackground gameBackground;

    private MusicPlayer musicPlayer;
    private MusicPlayer sfxPlayer;

    private int maxRenderFPS;

    private int realPhysicsFPS;
    private int realRenderFPS;

    public ArrayList<Tetris> tetrises;

    public GamePanel(int width, int height, int renderHeight, int horizontalPadding, int verticalPadding) {
        GamePanel.instance = this;

        gameWidth = width;
        gameHeight = height;

        this.renderHeight = renderHeight;

        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;

        this.setFocusable(true); //make everything in this class appear on the screen
        keyboardInput = new KeyboardInput();
        this.addKeyListener(keyboardInput);
        this.setPreferredSize(new Dimension(gameWidth, gameHeight));

        //Display Main Menu
        displayGui(new GuiWelcome());

        this.addMouseListener(new MouseInput());
        MouseInput.setScale((double)renderHeight/1080, horizontalPadding, verticalPadding);

        gameBackground = new GameBackground();
        gameSettings = new GameSettings();

        musicPlayer = new MusicPlayer();
        sfxPlayer = new MusicPlayer();

        musicPlayer.loadMusic(Assets.NIGHT_SNOW);
        musicPlayer.playMusic();
        musicPlayer.setLoop(true);
        musicPlayer.changeVolume(0.9);

        sfxPlayer.loadMusic(Assets.SFX.SILENCE);
        sfxPlayer.playMusic();

        tetrises = new ArrayList<>();


        gameThread = new Thread(this);
        gameThread.start();

    }

    public void run() {
        //the CPU runs our game code too quickly - we need to slow it down! The following lines of code "force" the computer to get stuck in a loop for short intervals between calling other methods to update the screen.
        long lastTime = System.nanoTime();

        double deltaRender = 0;
        double deltaPhysics = 0;
        long now;

        long previousFPSTime = System.nanoTime();
        int countUpdate = 0, countRender = 0;

        while (true) { //this is the infinite game loop
            now = System.nanoTime();
            deltaRender = deltaRender + (now - lastTime) / renderNS;
            deltaPhysics = deltaPhysics + (now - lastTime) / physicsNS;
            lastTime = now;

            //only move objects around  if enough time has passed
            if (deltaPhysics >= 1) {
            	
                update();
                countUpdate++;
                deltaPhysics--;
            }
            //only update the screen if enough time has passed
            if(deltaRender >= 1) {
                repaint();
                countRender++;
                deltaRender--;
            }

            if(System.nanoTime() - previousFPSTime >= 1000000000) {
                previousFPSTime = System.nanoTime();
                this.realRenderFPS = countRender;
                this.realPhysicsFPS = countUpdate;
                countUpdate = 0;
                countRender = 0;
            }
        }
    }

    public void update(){
        keyboardInput.update();
        for(Tetris tetris: tetrises){
            tetris.update();
        }
    }

    public void setPhysicsFPS(int fps){
        physicsNS = 1e9 / fps;
    }

    public void setRenderFPS(int fps){
        this.maxRenderFPS = fps;
        renderNS = 1e9 / fps;
    }

    public void paint(Graphics g){
        image = createImage(INTERNAL_WIDTH, INTERNAL_HEIGHT); //draw off screen
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        //TODO: TEST: test if this is needed
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        //Enable subpixel antialiasing for better legibility on LCD Screens
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        draw(g2d);//update the positions of everything on the screen

        g.drawImage(image, horizontalPadding, verticalPadding, gameWidth-horizontalPadding, gameHeight-verticalPadding, 0, 0, 1920, 1080, this);
    }

    public void draw(Graphics2D g){
        this.gui.draw(g);
    }

    public void displayGui(Gui menu){
        this.gui = menu;
    }

    public Gui getGui(){
    	return this.gui;
    }

    public GameBackground getGameBackground(){
        return this.gameBackground;
    }

    //Getter for Game Panel instance
    public static GamePanel getGamePanel(){
        return GamePanel.instance;
    }

    public GameSettings getSettings(){
        return gameSettings;
    }


    public int getRealPhysicsFPS(){
        return realPhysicsFPS;
    }

    public int getRealRenderFPS(){
        return realRenderFPS;
    }

    public int getMaxRenderFPS(){
        return maxRenderFPS;
    }

    public MusicPlayer getMusicPlayer(){
    	return musicPlayer;
    }

    public MusicPlayer getSFXPlayer(){
    	return sfxPlayer;
    }

    //Stop the game
    public void exitGame(){
        System.exit(0);
    }
}
