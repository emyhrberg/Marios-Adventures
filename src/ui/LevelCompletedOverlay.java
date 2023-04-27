package ui;

import helpers.ImageLoader;
import main.Game;
import main.State;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * When in level completed state, this class handles drawing a level completed image
 */
public class LevelCompletedOverlay extends State {
    // ====== Variables ======
    private static final BufferedImage LEVEL_COMPLETED_IMAGE = ImageLoader.loadImage("/ui/level-completed.png");

    // ====== Constructor ======
    public LevelCompletedOverlay(Game game) {
	    super(game);
    }

    public void drawLevelCompleted(Graphics g) {
	    drawImage(g, LEVEL_COMPLETED_IMAGE);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameGoToMenu();
            case KeyEvent.VK_ENTER -> game.getPlaying().resetGameLoadNextLevel();
        }
    }
}