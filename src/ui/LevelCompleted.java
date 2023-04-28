package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * When in level completed state, this class handles drawing a level completed image
 */
public class LevelCompleted extends GameState {

    // ====== Variables ======
    private static final BufferedImage LEVEL_COMPLETED_IMAGE = ImageLoader.loadImage("/ui/level-completed.png");

    // ====== Constructor ======
    public LevelCompleted(Game game) {
	    super(game);
    }

    public void drawLevelCompleted(Graphics g) {

    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameGoToMenu();
            case KeyEvent.VK_ENTER -> game.getPlaying().resetGameLoadNextLevel();
        }
    }
}