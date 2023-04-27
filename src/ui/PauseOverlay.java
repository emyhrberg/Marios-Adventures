package ui;

import helpers.ImageLoader;
import main.Game;
import main.State;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import static constants.GameState.PLAYING;

/**
 * When in paused state, this class handles drawing a pause image
 */
public class PauseOverlay extends State {
    // ====== Variables ======
    private static final BufferedImage PAUSED_IMAGE = ImageLoader.loadImage("/ui/pause.png");

    // ====== Constructor ======
    public PauseOverlay(Game game) {
		super(game);
    }

    public void drawPause(Graphics g) {
		final int imageW = (int) (500 * Game.SCALE);
		final int imageH = (int) (500 * Game.SCALE);
		final int imageX = Game.GAME_WIDTH / 2 - imageH / 2;
		final int imageY = Game.GAME_HEIGHT / 2 - imageH / 2;

		// Draw the image
		g.drawImage(PAUSED_IMAGE, imageX, imageY, imageW, imageH, null);
    }

    public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameGoToMenu();
			case KeyEvent.VK_P, KeyEvent.VK_ENTER -> game.setGameState(PLAYING);
		}
    }
}
