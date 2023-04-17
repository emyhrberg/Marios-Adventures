package main;

import helpers.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

/**
 * This class handles showing the game by using the package javax.Swing
 * Uses the game component which consists of the entire game
 */
public class GameFrame extends JFrame {

    // ====== Constructor ======
    public GameFrame(GameComponent gameComponent) {
		// title and icon
		setTitle("Mario's Adventures!");
		BufferedImage image = ImageLoader.loadImage("icon6.png");
		setIconImage(image);

		// settings
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);
//		setUndecorated(true);
		setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

		// add and pack
		add(gameComponent);
		pack();

		// center and show
		setLocationRelativeTo(null);
		setVisible(true);
    }

}
