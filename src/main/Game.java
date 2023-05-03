package main;

import constants.GameState;
import helpers.SoundPlayer;
import ui.Menu;
import ui.*;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static constants.GameState.*;

/**
 * This class implements a runnable Game
 * Handles game loop, game component, game window
 * Updates and renders game related objects
 * Main game loop: Overrides the run method
 * Implements a game loop with a fixed frame rate (FPS) and update rate (UPS)
 * Calculates the time between each frame and update and waits for a specific amount of time before rendering/updating the game again.
 * Allows the game to run smoothly and efficiently by avoiding excessive CPU usage
 */
public class Game implements Runnable {

    public static final boolean DEBUG = false;

    // ====== Game loop ======
    public static final int UPS         = 180;
    public static final int FPS         = 60;
    private long previousTime           = System.nanoTime();
    private double deltaU               = 0;
    private double deltaF               = 0;
    private static final double TIME_PER_FRAME = 1000000000.0 / FPS;
    private static final double TIMER_PER_UPDATE = 1000000000.0 / UPS;
    private long lastCheck;

    // ====== Game Variables ======
    private final GameComponent gameComponent;
    private final GameFrame gameFrame;
    private Thread gameThread;
    private GameState gameState;
    private GameState prevState;

    // ====== Game States ====== note: construct Menu first because it has all SCALE variables
    private final Menu menu                     = new Menu(this);
    private final Playing playing               = new Playing(this);
    private final Paused paused                 = new Paused(this);
    private final LevelCompleted levelCompleted = new LevelCompleted(this);
    private final GameCompleted gameCompleted   = new GameCompleted(this);
    private final GameOver gameOver             = new GameOver(this);
    private final Options options               = new Options(this);

    // ====== Sounds ======
    private Clip menuClip;
    private Clip playingClip;
    private Clip gameOverClip;
    private Clip gameCompletedClip;
    private int playingClipFrame;

    // ====== Overlay settings =====
    private boolean allowPress;
    private boolean allowDraw;
    private long lastStateCheck;
    private boolean isFirstTime = false;
    private static final int DISALLOW_DRAW_WAIT = 2300;
    private static final int DISALLOW_KEY_WAIT  = 500;
    private static final int GO_PLAYING_WAIT    = 9000;

    // ====== Constructor ======
    public Game() {
        gameComponent = new GameComponent(this);
        gameFrame = new GameFrame(gameComponent);

        if (menu.getUserW() == 1920) {
            goFullScreen();
        }

        // Set game state on launch
        gameState = MENU;
        menuClip = SoundPlayer.playSound("/sounds/menu.wav");

        // Start game loop
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        switch (gameState) {
            case MENU:
                gameFrame.setCursor(gameFrame.getDefaultCursor());
                menu.update();
                break;
            case PLAYING:
                gameFrame.setCursor(gameFrame.getBlankCursor());
                playing.update();
                break;
            case PAUSED:
                gameFrame.setCursor(gameFrame.getDefaultCursor());
                paused.update();
                break;
            case LEVEL_COMPLETED:
            case GAME_COMPLETED:
            case GAME_OVER:
                long timeSinceLastCheck = System.currentTimeMillis() - lastStateCheck;
                if (timeSinceLastCheck >= DISALLOW_KEY_WAIT) {
                    allowPress = true;
                }
                if (timeSinceLastCheck >= DISALLOW_DRAW_WAIT) {
                    allowDraw = true;
                }
                if (timeSinceLastCheck >= GO_PLAYING_WAIT) {
                    playing.resetGameGoToPlaying();
                }
                break;
            case OPTIONS:
                options.update();
                break;
            default:
                // Handle the default case here
                break;
        }
    }

    public void draw(Graphics g) {
        if (gameState == null) {
            System.err.println("Error: no gamestate??? game.draw()");
            return;
        }

        switch (gameState) {
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case LEVEL_COMPLETED:
                playing.drawBlur(g);
                levelCompleted.drawLevelCompleted(g);
                break;
            case PAUSED:
                playing.drawBlur(g);
                paused.draw(g);
                break;
            case GAME_COMPLETED:
                playing.drawBlur(g);
                gameCompleted.drawGameCompleted(g);
                break;
            case GAME_OVER:
                playing.draw(g);
                gameOver.drawGameOver(g);
                break;
            case OPTIONS:
                if (prevState == MENU) {
                    menu.draw(g);
                    options.draw(g);
                } else {
                    playing.drawBlur(g);
                    options.draw(g);
                }
                break;
            default:
                // Handle the default case here
                break;
        }
    }

    public void run() {
        // Main loop
        while (true) {
            long currentTime = System.nanoTime();

            // Calculate delta time
            deltaU += (currentTime - previousTime) / TIMER_PER_UPDATE;
            deltaF += (currentTime - previousTime) / TIME_PER_FRAME;
            previousTime = currentTime;

            // Update the game state
            if (deltaU >= 1) {
                update();
                deltaU--;
            }

            // Render the game by repainting the gameComponent
            if (deltaF >= 1) {
                gameComponent.repaint();
                deltaF--;
            }

            // Set last check and reset frames and updates
            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
            }
        }
    }

    // ====== Game methods ======

    public void setGameState(GameState gameState) {
        // Set the game state, and also save previous game state
        prevState = this.gameState;
        this.gameState = gameState;
        isFirstTime = true;

        playGameStateSounds();
        handleWaitStates();
    }

    private void playGameStateSounds() {
        if (gameState == PLAYING) {
            stopSounds();
            playingClip = SoundPlayer.playSoundLoop("/sounds/playing.wav");
            if (prevState == PAUSED && playingClip != null) {
                playingClip.setFramePosition(playingClipFrame);
            }
        }

        else if (gameState == MENU) {
            if (prevState == OPTIONS)
                return;
            stopSounds();
            menuClip = SoundPlayer.playSoundLoop("/sounds/menu.wav");
        }

        else if (gameState == PAUSED) {
            if (playingClip != null) {
                playingClipFrame = playingClip.getFramePosition();
            }
            stopSounds();
        }

        else if (gameState == GAME_OVER) {
            stopSounds();
            gameOverClip = SoundPlayer.playSound("/sounds/gameover.wav");
        }

        else if (gameState == GAME_COMPLETED || gameState == LEVEL_COMPLETED) {
            stopSounds();
            gameCompletedClip = SoundPlayer.playSound("/sounds/gamecompleted.wav");
        }
    }

    private void stopSounds() {
        if (playingClip != null)
            playingClip.close();

        if (menuClip != null)
            menuClip.close();

        if (gameCompletedClip != null)
            gameCompletedClip.close();

        if (gameOverClip != null)
            gameOverClip.close();
    }

    private void handleWaitStates() {
        if (gameState == GAME_COMPLETED || gameState == GAME_OVER || gameState == LEVEL_COMPLETED) {
            allowDraw = false;
            allowPress = false;
            lastStateCheck = System.currentTimeMillis();
        }
    }

    ///////////////// FULL SCREEN ////////////////

    private void goFullScreen() {
        // dispose old frame
        Window window = SwingUtilities.windowForComponent(gameComponent);
        JFrame frame = (JFrame) window;
        frame.dispose();

        // set fullscreen on new frame
        frame.setUndecorated(true);
        frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(window);
        frame.add(gameComponent);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        fullScreen = true;
    }

    private void goWindowed() {
        // dispose old frame
        Window window = SwingUtilities.windowForComponent(gameComponent);
        JFrame frame = (JFrame) window;
        frame.dispose();

        // set windowed on new frame
        frame.setUndecorated(false);
        frame.getGraphicsConfiguration().getDevice().setFullScreenWindow(null);
        frame.add(gameComponent);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        fullScreen = false;
    }

    // Declare boolean variables to track the state of each button
    private boolean fullScreen = false;

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F)
            fullScreen = !fullScreen;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F)
            if (fullScreen)
                goFullScreen();
            else
                goWindowed();
    }

    // ====== Getters && Setters ======

    public GameState getPrevState() {
        return prevState;
    }

    public Options getOptions() {
        return options;
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

    public Paused getPauseState() {
        return paused;
    }

    public LevelCompleted getLevelCompletedState() {
        return levelCompleted;
    }

    public GameCompleted getGameCompletedState() {
        return gameCompleted;
    }

    public GameOver getGameOverState() {
        return gameOver;
    }

    public boolean isKeyNotAllowed() {
        return !allowPress;
    }

    public boolean isDrawNotAllowed() {
        return !allowDraw;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    // ====== Getters Sounds ======

    public Clip getMenuClip() {
        return menuClip;
    }

    public Clip getPlayingClip() {
        return playingClip;
    }

    public Clip getGameOverClip() {
        return gameOverClip;
    }

    public Clip getGameCompletedClip() {
        return gameCompletedClip;
    }
}
