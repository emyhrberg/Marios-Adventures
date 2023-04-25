package overlays;

import helpers.GifLoader;
import helpers.ImageLoader;
import main.Game;
import main.State;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * When in game completed state, this class handles drawing a game over gif
 */
public class GameCompletedOverlay extends State {
    // ======= Variables =======
    private static final BufferedImage YOU_WIN_IMAGE = ImageLoader.loadImage("/images/overlay_you_win.png");

    // ====== Constructor ======
    public GameCompletedOverlay(Game game) {
		super(game);
    }

    public void drawGameCompleted(Graphics g) {
		if (!game.isDrawAllowed()) {
//			return;
		}
		// Draw the gif at the center of the screen
		GifLoader gifImage = GifLoader.loadGif("overlay_stars.gif");
		Graphics2D g2d = (Graphics2D) g.create();
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

	public void keyPressed() {
		if (!game.isPressAllowed()) {
			return;
		}

		// Reset game nd go to 1st level
		game.getPlaying().getLevelManager().setLevelIndex(0);
		game.getPlaying().resetGameGoToMenu();
	}
}
