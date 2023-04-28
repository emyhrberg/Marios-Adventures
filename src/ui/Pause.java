package ui;

import helpers.FontLoader;
import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import static constants.GameState.PLAYING;

/**
 * Pause game-state, draws the overlay
 */
public class Pause extends GameState {

	// Title
	private static final Font CUSTOM_FONT = FontLoader.loadFont("/fonts/inside-out.ttf");
	private static final float FONT_SIZE_BIG = 92f;
	private static final String title = "pause menu";
	private static final Color RED = new Color(232, 67, 33); // Color for highlighted text
	private static final Color WHITE = new Color(224, 224, 244); // Color for default text
	private static final Color BLACK = new Color(17, 17, 17); // Color for stroke
	private static final BasicStroke STROKE_SIZE = new BasicStroke(5f);

	// options
	private static final float FONT_SIZE = 56f;
	private static final String RESUME = "resume";
	private static final String QUIT = "save & quit";
	private int selectedIndex = 0; // Currently selected option index

	// y pos
	private static final int TITLE_Y 	= 280;
	private int OPTIONS_Y 				= 450;
	private static final int PAD 		= 70;

    public Pause(Game game) {
		super(game);
    }

	public void drawPause(Graphics g) {
		drawPauseTitle(g);
		drawOptions(g);
	}

	private void drawOptions(Graphics g) {
		drawOption(g, RESUME, 0, 0);
		drawOption(g, QUIT, 1, 1);
	}

	private void drawPauseTitle(Graphics g) {
		// font and shapes
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(CUSTOM_FONT.deriveFont(FONT_SIZE_BIG));
		TextLayout tl = new TextLayout(title, g.getFont(), g2d.getFontRenderContext());
		Shape s = tl.getOutline(null);

		// position
		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(title);
		int x = (Game.GAME_WIDTH - w) / 2; // Centered X position
		AffineTransform transform = AffineTransform.getTranslateInstance(x, TITLE_Y);
		g2d.transform(transform);

		// Draw black stroke
		g2d.setStroke(STROKE_SIZE);
		g2d.setColor(BLACK);
		g2d.draw(s);

		// draw white fill
		g2d.setColor(WHITE);
		g2d.fill(s);

		// Restore the original transform
		g2d.setTransform(new AffineTransform());
	}

	private void drawOption(Graphics g, String option, int index, int yIndex) {
		// font and shapes
		Graphics2D g2d = (Graphics2D) g;
		g.setFont(CUSTOM_FONT.deriveFont(FONT_SIZE));
		TextLayout tl = new TextLayout(option, g.getFont(), g2d.getFontRenderContext());
		Shape s = tl.getOutline(null);

		// position
		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(option);
		int x = (Game.GAME_WIDTH - w) / 2; // Centered X position
		if (yIndex == 0) { // first
			OPTIONS_Y -= PAD;
		} else if (yIndex == 1) {
			OPTIONS_Y += PAD; // second
		}
		AffineTransform transform = AffineTransform.getTranslateInstance(x, OPTIONS_Y);
		g2d.transform(transform);

		// Draw black stroke
		g2d.setStroke(STROKE_SIZE);
		g2d.setColor(BLACK);
		g2d.draw(s);

		// Draw white or highlighted color
		if (index == selectedIndex) {
			g2d.setColor(RED);
		} else {
			g2d.setColor(WHITE);
		}
		g2d.fill(s);

		// Restore the original transform
		g2d.setTransform(new AffineTransform());
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			// Scrolling infinite!
			case KeyEvent.VK_W, KeyEvent.VK_UP -> selectedIndex = (selectedIndex + 1) % 2;
			case KeyEvent.VK_S, KeyEvent.VK_DOWN -> selectedIndex = (selectedIndex - 1 + 2) % 2;

			// Only up and down scroll allowed!
//			case KeyEvent.VK_W -> selectedIndex = Math.max(selectedIndex - 1, 0);
//			case KeyEvent.VK_S -> selectedIndex = Math.min(selectedIndex + 1, 1);
			case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> handleSelection();
			case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameSavePoint();
		}
	}

	private void handleSelection() {
		if (selectedIndex == 0) {
			// Option "PLAY" is selected
			game.setGameState(PLAYING);
		} else if (selectedIndex == 1) {
			game.getPlaying().resetGameSavePoint();
		}
		selectedIndex = 0;
	}
}
