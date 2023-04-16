package overlays;

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
    private static final BufferedImage LEVEL_COMPLETED_IMAGE = ImageLoader.loadImage("overlay_level_completed.png");

    // ====== Constructor ======
    public LevelCompletedOverlay(Game game) {
	    super(game);
    }

    public void drawLevelCompleted(Graphics g) {
	    drawImage(g, LEVEL_COMPLETED_IMAGE);
    }

    public void keyPressed(KeyEvent e) {
        if (game.isNotAllowedPress())
            return; // ignore key press

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameGoToMenu();
            case KeyEvent.VK_ENTER -> game.getPlaying().resetGameLoadNextLevel();
        }
    }
}
