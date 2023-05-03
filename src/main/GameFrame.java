package main;

import helpers.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

import static constants.GameState.PAUSED;
import static constants.GameState.PLAYING;
import static ui.Menu.GAME_HEIGHT;
import static ui.Menu.GAME_WIDTH;

/**
 * This class handles showing the game by using the package javax.Swing
 * Uses the game component which consists of the entire game
 */
public class GameFrame extends JFrame {

	private static final BufferedImage icon = ImageLoader.loadImage("/ui/mario-icon.png");
	private final Cursor blankCursor;
	private final Cursor defaultCursor;

    public GameFrame(final GameComponent gameComponent) {
		// title and icon
		setTitle("Mario's Adventures!");
		setIconImage(icon);

		// settings
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		// cursor
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(blankCursor);

		// add actual game
		add(gameComponent);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		addWindowFocusListener(new WindowFocusListener() {
			@Override public void windowLostFocus(WindowEvent e) {
				// Lost focus, pause
				if (gameComponent.getGame().getGameState() == PLAYING) {
					gameComponent.getGame().setGameState(PAUSED);
				}
			}
			@Override public void windowGainedFocus(WindowEvent e) {
			}
		});
    }

	public Cursor getBlankCursor() {
		return blankCursor;
	}

	public Cursor getDefaultCursor() {
		return defaultCursor;
	}
}
