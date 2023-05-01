package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static ui.Menu.*;

/**
 * When in game completed state, this class handles drawing a win gif
 */
public class GameCompleted extends GameState {

    // ======= Variables =======
    private static final BufferedImage YOU_WIN_IMAGE = ImageLoader.loadImage("/ui/you-win.png");

    // ====== Constructor ======
    public GameCompleted(Game game) {
		super(game);
    }

    public void drawGameCompleted(Graphics g) {
		if (game.isDrawNotAllowed()) {
//			return;
		}

		// Draw the overlay image
		final int imageW = (int) (500 * SCALE);
		final int imageH = (int) (500 * SCALE);
		final int imageX = GAME_WIDTH / 2 - imageH / 2;
		final int imageY = GAME_HEIGHT / 2 - imageH / 2;

		// Draw the image
		g.drawImage(YOU_WIN_IMAGE, imageX, imageY, imageW, imageH, null);
	}

	public void keyPressed(KeyEvent e) {
		if (game.isKeyNotAllowed()) {
			return;
		}

		// Reset game and go to 1st level
		game.getPlaying().getLevelManager().setLevelIndex(0);
		game.getPlaying().resetGameGoToMenu();
	}
}
