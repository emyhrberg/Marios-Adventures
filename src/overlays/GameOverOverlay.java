package overlays;

import helpers.ImageLoader;
import main.Game;
import main.State;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * When in game over state, this class handles drawing game over image
 */
public class GameOverOverlay extends State {
    // ====== Variables ======
    private static final BufferedImage GAME_OVER_IMAGE = ImageLoader.loadImage("overlay_game_over.png");

    // ====== Constructor ======
    public GameOverOverlay(Game game) {
	    super(game);
    }

    public void drawGameOver(Graphics g) {
	    drawImage(g, GAME_OVER_IMAGE);
    }

    public void keyPressed(KeyEvent e) {
        if (game.isNotAllowedPress())
            return; // ignore key press

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameGoToMenu();
            case KeyEvent.VK_ENTER -> game.getPlaying().resetGameGoToPlaying();
        }
    }

}
