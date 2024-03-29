package main;

import constants.GameState;
import helpers.Sound;
import ui.Menu;
import ui.*;

import javax.sound.sampled.Clip;
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
    private GameState gameState;
    private GameState prevState;

    // ====== Game States ====== note: construct Menu first because it has all SCALE variables
    private final Menu menu                     = new Menu(this);
    private final Playing playing               = new Playing(this);
    private final Paused paused                 = new Paused(this);
    private final LevelCompleted levelCompleted = new LevelCompleted(this);
    private final GameCompleted gameCompleted   = new GameCompleted(this);
    private final GameOver gameOver             = new GameOver(this);
    private final Volume volume                 = new Volume(this);
    private final Controls controls             = new Controls(this);

    // ====== Sounds ======
    private Clip menuClip;
    private Clip playingClip;
    private Clip gameOverClip;
    private Clip gameCompletedClip;

    // ====== Overlay settings =====
    private boolean allowPress;
    private boolean allowDraw;
    private long lastStateCheck;
    private boolean isFirstTime = false;
    private static final int DISALLOW_DRAW_WAIT = 1500;
    private static final int DISALLOW_KEY_WAIT  = 1500;
    private static final int GO_PLAYING_WAIT    = 9000; // after game over, go to playing after 9 seconds

    // ====== Constructor ======
    public Game() {
        gameComponent = new GameComponent(this);
        gameFrame = new GameFrame(gameComponent);

        if (menu.getUserW() == 1920) {
//            goFullScreen();
        }

        // Set game state on launch
        gameState = MENU;
        menuClip = Sound.playSoundLoop("/sounds/menu.wav");

        // Start game loop
        Thread gameThread = new Thread(this);
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
            case VOLUME:
                volume.update(); // empty
                break;
            case CONTROLS:
                controls.update();
                break;
            default:
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
            case VOLUME:
                if (prevState == MENU) {
                    menu.draw(g);
                    volume.draw(g);
                } else {
                    playing.drawBlur(g);
                    volume.draw(g);
                }
                break;
            case CONTROLS:
                if (prevState == MENU) {
                    menu.draw(g);
                    controls.draw(g);
                } else {
                    playing.drawBlur(g);
                    controls.draw(g);
                }
                break;
            default:
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
            if (prevState == PAUSED || prevState == VOLUME || prevState == CONTROLS)
                return;
            stopSounds();
            playingClip = Sound.playSoundLoop("/sounds/playing.wav");
        }

        else if (gameState == MENU) {
            if (prevState == VOLUME || prevState == CONTROLS)
                return;
            stopSounds();
            menuClip = Sound.playSoundLoop("/sounds/menu.wav");
        }

        else if (gameState == GAME_OVER) {
            stopSounds();
            gameOverClip = Sound.play("/sounds/gameover.wav");
        }

        else if (gameState == GAME_COMPLETED || gameState == LEVEL_COMPLETED) {
            stopSounds();
            gameCompletedClip = Sound.play("/sounds/gamecompleted.wav");
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

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F)
            gameFrame.resizeWindow();
    }

    // ====== Getters && Setters ======

    public GameState getPrevState() {
        return prevState;
    }

    public Volume getVolume() {
        return volume;
    }

    public Controls getControls() {
        return controls;
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
}
