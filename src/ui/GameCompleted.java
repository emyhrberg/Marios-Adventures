package ui;

import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

import static ui.Menu.*;

/**
 * When in game completed state, this class handles drawing a win gif
 */
public class GameCompleted extends GameState {

    // ======= Variables =======

    // ====== Constructor ======
    public GameCompleted(Game game) {
		super(game);
    }

    public void drawGameCompleted(Graphics g) {
		String s = "Game completed! Press any key to continue";
		g.setFont(g.getFont().deriveFont(80f));
		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(s);
		int h = fm.getHeight();
		int x = GAME_WIDTH / 2 - w / 2;
		int y = GAME_HEIGHT / 2 - h / 2;
		g.drawString(s, x, y);
	}

	public void keyPressed(KeyEvent e) {
		if (game.isKeyNotAllowed()) {
			return;
		}

		// Reset game and go to 1st level
		game.getPlaying().resetGameGoToMenu();
	}
}
