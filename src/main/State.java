package main;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.*;

/**
 * Superclass for all gamestates
 * Constructs a state with a game
 * The base class for all game states
 * A game state represents a part of the game, like menu or playing
 * Each state uses the game instance to determine its own behavior
 */
public class State {

    // ====== Variables ======
    protected Game game;

    // ====== Alpha ======
    private int alpha = 0;

    // ====== Constructor ======
    protected State(Game game) {
	this.game = game;
    }

    /**
     * This method draws a given image in the center of the screen
     */
    protected void drawImage(Graphics g, BufferedImage img) {
        // Values for alpha
        alpha += 5;
        if (alpha > 240)
            alpha = 240;

        // Draw a rectangle with opacity
        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Set image size and position
        final int imageW = (int) (500 * SCALE);
        final int imageH = (int) (500 * SCALE);
        final int imageX = GAME_WIDTH / 2 - imageH / 2;
        final int imageY = GAME_HEIGHT / 2 - imageH / 2;

        // Draw the image
        g.drawImage(img, imageX, imageY, imageW, imageH, null);
    }
}
