package main;

import constants.GameState;
import helpers.SoundLoader;
import ui.Menu;
import ui.*;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static constants.GameState.*;
import static ui.Paused.isMuted;

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
    private static final int DISALLOW_KEY_WAIT  = 2300;
    private static final int GO_PLAYING_WAIT    = 8500;

    // ====== Constructor ======
    public Game() {
        gameComponent = new GameComponent(this);
        gameFrame = new GameFrame(gameComponent);

        // temp
        gameState = MENU;
        isMuted = true;

        if (menu.getUserW() == 1920) {
            goFullScreen();
        }

        // Set game state on launch
//        gameState = MENU;
//        menuClip = SoundLoader.playSound("/sounds/menu.wav");


        // Start game loop
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        switch (gameState) {
            case MENU:
                menu.update();
                break;
            case PLAYING:
                playing.update();
                break;
            case PAUSED:
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
            default:
                // Handle the default case here
                break;
        }
    }

    public void draw(Graphics g) {
        switch (gameState) {
            case MENU:
                menu.draw(g);
                break;
            case PLAYING:
                playing.draw(g);
                break;
            case LEVEL_COMPLETED:
                playing.draw(g);
                levelCompleted.drawLevelCompleted(g);
                break;
            case PAUSED:
                playing.drawBlur(g);
                paused.draw(g);
                break;
            case GAME_COMPLETED:
                playing.draw(g);
                gameCompleted.drawGameCompleted(g);
                break;
            case GAME_OVER:
                playing.draw(g);
                gameOver.drawGameOver(g);
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
            playingClip = SoundLoader.playSoundLoop("/sounds/playing.wav");
            if (prevState == PAUSED && playingClip != null) {
                playingClip.setFramePosition(playingClipFrame);
            }
        }

        else if (gameState == MENU) {
            stopSounds();
            menuClip = SoundLoader.playSoundLoop("/sounds/menu.wav");
        }

        else if (gameState == PAUSED) {
            if (playingClip != null) {
                playingClipFrame = playingClip.getFramePosition();
            }
            stopSounds();
        }

        else if (gameState == GAME_OVER) {
            stopSounds();
            gameOverClip = SoundLoader.playSound("/sounds/gameover.wav");
        }

        else if (gameState == GAME_COMPLETED) {
            stopSounds();
            gameCompletedClip = SoundLoader.playSound("/sounds/gamecompleted.wav");
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
    private boolean isAltPressed = false;
    private boolean isEnterPressed = false;
    private boolean altEnter = false;
    private boolean fullScreen = false;
    private boolean isF = false;

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int alt = KeyEvent.VK_ALT;
        int enter = KeyEvent.VK_ENTER;

        // temp
        if (key == KeyEvent.VK_F)
            isF = true;

        if (isF) {
            fullScreen = !fullScreen;
            if (fullScreen) {
                goFullScreen();
            } else {
                goWindowed();
            }
            isF = false;
        }

        if (key == alt) {
            isAltPressed = true;
        } else if (key == enter) {
            isEnterPressed = true;
        }

        // Check if both buttons are held at the same time
        if (isAltPressed && isEnterPressed && !altEnter) {
            fullScreen = !fullScreen;
            altEnter = true;
            
            if (fullScreen) {
                goFullScreen();
            } else {
                goWindowed();
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        int alt = KeyEvent.VK_ALT;
        int enter = KeyEvent.VK_ENTER;

        if (key == alt || key == enter) {
            isAltPressed = false;
            isEnterPressed = false;
            altEnter = false;
        }
    }

    // ====== Getters && Setters ======

    public GameComponent getGameComponent() {
        return gameComponent;
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
}
