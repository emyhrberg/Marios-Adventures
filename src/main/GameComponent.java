package main;

import helpers.KeyInput;
import helpers.MouseInput;
import helpers.MouseMotionInput;

import javax.swing.*;
import java.awt.*;

/**
 * This class holds the GameComponent which handles the panel
 * Adds user inputs like key and mouse
 * Draws everything in the game in paintComponent
 */
public class GameComponent extends JPanel {

    // ====== Variables ======
    private final Game game;

    // ====== Constructor ======
    public GameComponent(Game game) {
		this.game = game;

		// Set focusable to accept input
		setFocusable(true);

		// Add inputs
		addKeyListener(new KeyInput(game));
		addMouseListener(new MouseInput(game));
		addMouseMotionListener(new MouseMotionInput(game));
    }

    protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

        long drawStart;
        drawStart = System.nanoTime();

		game.draw(g);

        long drawEnd = System.nanoTime();
        long passed  = drawEnd - drawStart;
        System.out.println(passed);
    }

    public Game getGame() {
        return game;
    }
}
