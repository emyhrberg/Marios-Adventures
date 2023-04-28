package ui;

import helpers.GifLoader;
import helpers.ImageLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * When in game completed state, this class handles drawing a win gif
 */
public class GameCompleted extends GameState {

    // ======= Variables =======
    private static final BufferedImage YOU_WIN_IMAGE = ImageLoader.loadImage("/ui/you-win.png");
	private static final GifLoader gifImage = GifLoader.loadGif("/ui/stars.gif");


    // ====== Constructor ======
    public GameCompleted(Game game) {
		super(game);
    }

    public void drawGameCompleted(Graphics g) {
		if (game.isDrawNotAllowed()) {
//			return;
		}

		// Draw the gif at the center of the screen
		Graphics2D g2d = (Graphics2D) g.create();

		if (gifImage == null)
			return;

		g2d.translate(gifImage.getPosition().getWidth(), gifImage.getPosition().getHeight());
		g2d.drawImage(gifImage.getImageIcon().getImage(), gifImage.getTransform(), game.getGameComponent());

		// Draw the overlay image
		final int imageW = (int) (500 * Game.SCALE);
		final int imageH = (int) (500 * Game.SCALE);
		final int imageX = Game.GAME_WIDTH / 2 - imageH / 2;
		final int imageY = Game.GAME_HEIGHT / 2 - imageH / 2;

		// Draw the image
		g.drawImage(YOU_WIN_IMAGE, imageX, imageY, imageW, imageH, null);
	}

	public void keyPressed(KeyEvent e) {
		if (game.isKeyNotAllowed()) {
			System.out.println(e.getKeyCode());
//			return;
		}

		// Reset game and go to 1st level
		game.getPlaying().getLevelManager().setLevelIndex(0);
		game.getPlaying().resetGameGoToMenu();
	}
}
