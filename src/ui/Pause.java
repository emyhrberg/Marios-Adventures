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


	// box
	private static final Font CUSTOM_FONT = FontLoader.loadFont("/fonts/inside-out.ttf");
	private static final float FONT_SIZE = 48f;
	private static final int W = (int) (265 * Game.SCALE);
	private static final int H = (int) (220 * Game.SCALE);
	private static final int X = Game.GAME_WIDTH / 2 - W / 2;
	private static final int Y = Game.GAME_HEIGHT / 2 - H / 2;
	private static final int PAD = 50;
	private static final int ROUND = 25;
	private static final Color BOX_COLOR = new Color(10, 10, 10, 242);

	// options
	private static final String cont = "continue";
	private static final String quit = "save & quit";
	private static final Color HIGHLIGHT_COLOR = new Color(232, 67, 33); // Color for highlighted text
	private static final Color DEF_COLOR = new Color(224, 224, 244); // Color for default text
	private static final Color STROKE_COLOR = new Color(17, 17, 17); // Color for stroke text
	private static final BasicStroke STROKE_SIZE = new BasicStroke(4f);
	private int selectedIndex = 0; // Currently selected option index

    public Pause(Game game) {
		super(game);
    }

	public void drawPause(Graphics g) {
		drawBox(g);
		drawOptions(g);
	}

	private void drawBox(Graphics g) {
		g.setColor(BOX_COLOR);
		g.fillRoundRect(X, Y, W, H, ROUND, ROUND);
	}

	private void drawOptions(Graphics g) {
		drawOption(g, cont, 0, 0);
		drawOption(g, quit, 1, 1);
	}

	private void drawOption(Graphics g, String option, int index, int yIndex) {
		// font and shapes
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(CUSTOM_FONT);
		g.setFont(g.getFont().deriveFont(FONT_SIZE));
		TextLayout tl = new TextLayout(option, g.getFont(), g2d.getFontRenderContext());
		Shape s = tl.getOutline(null);

		// position
		FontMetrics fm = g.getFontMetrics();
		int w = fm.stringWidth(option);
		int h = fm.getHeight();
		int x = X + (W - w) / 2;
		int y = Y + (H - h) / 2 + fm.getAscent() - PAD; // continue Y
		if (yIndex == 0) {
			y = Y + (H - h) / 2 + fm.getAscent() - PAD; // continue Y
		} else if (yIndex == 1) {
			y = Y + (H - h) / 2 + fm.getAscent() + PAD; // quit Y
		}
		AffineTransform transform = AffineTransform.getTranslateInstance(x, y);
		g2d.transform(transform);

		// Draw black stroke
		g2d.setStroke(STROKE_SIZE);
		g2d.setColor(STROKE_COLOR);
		g2d.draw(s);

		// Draw white or highlighted color
		if (index == selectedIndex) {
			g2d.setColor(HIGHLIGHT_COLOR);
		} else {
			g2d.setColor(DEF_COLOR);
		}
		g2d.fill(s);

		// Restore the original transform
		g2d.setTransform(new AffineTransform());
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			// Scrolling infinite!
			case KeyEvent.VK_W -> selectedIndex = (selectedIndex + 1) % 2;
			case KeyEvent.VK_S -> selectedIndex = (selectedIndex - 1 + 2) % 2;

//			case KeyEvent.VK_W -> selectedIndex = Math.max(selectedIndex - 1, 0);
//			case KeyEvent.VK_S -> selectedIndex = Math.min(selectedIndex + 1, 1);
			case KeyEvent.VK_ENTER -> handleSelection();
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
