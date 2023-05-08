package ui;

import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static ui.Menu.*;

/**
 * When in game completed state, this class handles drawing game completed
 */
public class GameCompleted extends GameState {

    // ======= Variables =======
	private static final BufferedImage img = ImageLoader.loadImage("/ui/game-completed.png");

    // ====== Constructor ======
    public GameCompleted(Game game) {
		super(game);
    }

    public void drawGameCompleted(Graphics g) {
		int w = (int) (1920 / 2 * SCALE);
		int h = (int) (1080 / 2* SCALE);
		int x = GAME_WIDTH / 2 - w / 2;
		int y = GAME_HEIGHT / 2 - h / 2;
		g.drawImage(img, x, y, w, h, null);
	}

	public void keyPressed(KeyEvent e) {
		if (game.isKeyNotAllowed()) {
			return;
		}

		game.getPlaying().resetGameAndStartAtFirstLevel();
	}
}
