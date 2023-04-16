package main;

import constants.GameState;
import helpers.SoundLoader;
import misc.Menu;
import overlays.GameCompletedOverlay;
import overlays.GameOverOverlay;
import overlays.LevelCompletedOverlay;
import overlays.PauseOverlay;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;

import static constants.GameState.*;

/**
 * This class implements a runnable Game
 * Handles game loop, gamecomponent, gamewindow
 * Updates and renders game related objects
 */
public class Game implements Runnable {

    // ====== Updates Per Second ======
    public static final int UPS = 180;
    public static final int FPS = 60;

    // ====== Game Variables ======
    public static float SCALE                   = 1.35f;
    public static final int TILES_SIZE_DEFAULT  = 40;
    public static final int TILES_SIZE          = (int) (TILES_SIZE_DEFAULT * SCALE);
    public static final int TILES_IN_WIDTH      = 26;
    public static final int TILES_IN_HEIGHT     = 14;
    public static final int GAME_WIDTH          = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT         = TILES_SIZE * TILES_IN_HEIGHT;

    // ====== Game Variables ======
    private final GameComponent gameComponent;
    private GameState gameState;
    private GameState prevState;

    // ====== Game States ======
    private final Menu menu                                   = new Menu(this);
    private final Playing playing                             = new Playing(this);
    private final PauseOverlay pauseOverlay                   = new PauseOverlay(this);
    private final LevelCompletedOverlay levelCompletedOverlay = new LevelCompletedOverlay(this);
    private final GameCompletedOverlay gameCompletedOverlay   = new GameCompletedOverlay(this);
    private final GameOverOverlay gameOverOverlay             = new GameOverOverlay(this);

    // ====== Sounds and extras for game states ======
    private Clip menuClip, levelClip;
    private int levelClipFramePosition;
    private boolean allowPress = true;

    // ====== Constructor ======
    public Game() {
        // Create a GameFrame with the gameComponent
        gameComponent = new GameComponent(this);
        new GameFrame(gameComponent);

        // Set game state on launch
        gameState = PLAYING;

        // Get the available screen size (excluding the taskbar)
//        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        int userWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        if (userWidth == 1920) {
            SCALE = 1.85f;
        } else if (userWidth == 1280) {
            SCALE = 1.2f;
        }

        // Play main menu sound
        menuClip = SoundLoader.playAudio("menu.wav");

        startGameLoop();
    }

    public void update() {
        switch (gameState) {
            case MENU       -> menu.update();
            case PLAYING    -> playing.update();
        }
    }

    public void draw(Graphics g) {
        switch (gameState) {
            case MENU               -> menu.draw(g);
            case PLAYING            -> playing.draw(g);
            case LEVEL_COMPLETED    -> {
                playing.draw(g);
                levelCompletedOverlay.drawLevelCompleted(g);
            }
            case PAUSED             -> {
                playing.draw(g);
                pauseOverlay.drawPause(g);
            }
            case GAME_COMPLETED     -> {
                playing.draw(g);
                gameCompletedOverlay.drawGameCompleted(g);
            }
            case GAME_OVER          -> {
                playing.draw(g);
                gameOverOverlay.drawGameOver(g);
            }
        }
    }

    private void startGameLoop() {
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Overrides the run method
     * Implements a game loop with a fixed frame rate (FPS) and update rate (UPS)
     * Calculates the time between each frame and update and waits for a specific amount of time before rendering/updating the game again.
     * Allows the game to run smoothly and efficiently by avoiding excessive CPU usage
     */
    @Override
    public void run() {
        // Set the timer per frame and time per update
        double timePerFrame     = 1000000000.0 / FPS;
        double timePerUpdate    = 1000000000.0 / UPS;

        // Initialize some variables to calculate time
        long previousTime   = System.nanoTime();
        double deltaU       = 0;
        double deltaF       = 0;
        long lastCheck      = System.currentTimeMillis();

        // Main loop
        while (true) {
            long currentTime = System.nanoTime();

            // Calculate delta time
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
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
            final int oneSecond = 1000;
            if (System.currentTimeMillis() - lastCheck >= oneSecond) {
                lastCheck = System.currentTimeMillis();
            }
        }
    }

    // ====== Setters ======

    public void setGameState(GameState gameState) {
        // Set the game state, and also save previous game state
        prevState = this.gameState;
        this.gameState = gameState;

        playSounds();
        disableKeyPress();
    }

    // ====== Game methods ======

    private void playSounds() {
        // Close menu sound or open it
        if (gameState == MENU) {
            menuClip = SoundLoader.playAudio("menu.wav");
        } else {
            if (menuClip != null) {
                menuClip.close();
            }
        }

        if (gameState == PLAYING && levelClip == null) {
            // Start level track if not already playing
            levelClip = SoundLoader.playAudio("playing.wav");

            // Resume level track if coming from paused
            if (prevState == PAUSED) {
                assert levelClip != null;
                levelClip.setFramePosition(levelClipFramePosition);
            }
        } else if (gameState == PAUSED && levelClip != null) {
            levelClipFramePosition = levelClip.getFramePosition();
            levelClip.stop();
            levelClip = null;
        } else {
            // Stop level track if playing
            if (levelClip != null) {
                levelClip.stop();
                levelClip = null;
            }

            // Play appropriate sound for current state
            if (gameState == GAME_OVER) {
                SoundLoader.playAudio("game_over.wav");
            } else if (gameState == LEVEL_COMPLETED) {
                SoundLoader.playAudio("level_completed.wav", 0.5);
            } else if (gameState == GAME_COMPLETED) {
                SoundLoader.playAudio("game_completed.wav");
            }
        }
    }

    private void disableKeyPress() {
        if (gameState == GAME_COMPLETED || gameState == GAME_OVER || gameState == LEVEL_COMPLETED) {
            State.setFirstTime(true);
            allowPress = false;
            Timer timer = new Timer(1500, e -> {
                allowPress = true;
                ((Timer) e.getSource()).stop();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    // ====== Getters ======

    public static float getSCALE() {
        return SCALE;
    }

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

    public PauseOverlay getPauseOverlay() {
        return pauseOverlay;
    }

    public LevelCompletedOverlay getLevelCompletedOverlay() {
        return levelCompletedOverlay;
    }

    public GameCompletedOverlay getGameCompletedOverlay() {
        return gameCompletedOverlay;
    }

    public GameOverOverlay getGameOverOverlay() {
        return gameOverOverlay;
    }

    public boolean isNotAllowedPress() {
        return !allowPress;
    }
}
