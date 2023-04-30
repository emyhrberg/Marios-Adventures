package main;

import constants.GameState;
import helpers.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

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
		System.out.println("User size: " + w + " x " + h);
		System.out.println("Game size: " + Game.GAME_WIDTH + " x " + Game.GAME_HEIGHT);

		// settings
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setPreferredSize(new Dimension(Game.GAME_WIDTH, Game.GAME_HEIGHT));

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
