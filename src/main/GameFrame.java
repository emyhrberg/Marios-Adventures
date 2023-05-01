package main;

import constants.GameState;
import helpers.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

import static ui.Menu.GAME_HEIGHT;
import static ui.Menu.GAME_WIDTH;

/**
 * This class handles showing the game by using the package javax.Swing
 * Uses the game component which consists of the entire game
 */
public class GameFrame extends JFrame {

	private static final BufferedImage icon = ImageLoader.loadImage("/ui/mario-icon.png");

    public GameFrame(final GameComponent gameComponent) {
		// title and icon
		setTitle("Mario's Adventures!");
		setIconImage(icon);

		// check user screen size
		int w = Toolkit.getDefaultToolkit().getScreenSize().width;
		int h = Toolkit.getDefaultToolkit().getScreenSize().height;
		System.out.printf("--------------------------------%n");
		System.out.printf("| %-6s | %-6s | %6s |%n", "", "WIDTH", "HEIGHT");
		System.out.printf("--------------------------------%n");
		System.out.printf("| %-6s | %-6s | %6d |%n", "User", w,  h);
		System.out.printf("| %-6s | %-6s | %6d |%n", "Play", GAME_WIDTH, GAME_HEIGHT);
		System.out.printf("--------------------------------%n");

		// settings
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		// add and pack
		add(gameComponent);
		pack();

		// center and show
		setLocationRelativeTo(null);
		setVisible(true);

		addWindowFocusListener(new WindowFocusListener() {
			@Override public void windowLostFocus(WindowEvent e) {
				// Lost focus, pause
				if (gameComponent.getGame().getGameState() == GameState.PLAYING) {
//					gameComponent.getGame().setGameState(GameState.PAUSED);
				}
			}
			@Override public void windowGainedFocus(WindowEvent e) {
			}
		});
    }
}
