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

	private static final Font CUSTOM_FONT = FontLoader.loadFont("/fonts/inside-out.ttf");
	private static final int W = 1920/4;
	private static final int H = 1080/4;
	private static final int X = Game.GAME_WIDTH / 2 - W / 2;
	private static final int Y = Game.GAME_HEIGHT / 2 - H / 2;

	private static final String cont = "continue";
	private static final String quit = "save & quit";

    public Pause(Game game) {
		super(game);
    }

    public void drawPause(Graphics g) {
		drawBox(g);
		drawContinue(g);
		drawQuit(g);
    }

	private void drawBox(Graphics g) {
		g.setColor(new Color(10,10,10,180));
		g.fillRoundRect(X, Y, W, H, 10,10);
	}

	private static final int PAD = 50;

	private void drawContinue(Graphics g) {
		g.setFont(CUSTOM_FONT);
		Graphics2D g2d = (Graphics2D) g;
		g.setFont(g.getFont().deriveFont(58f));
		TextLayout tl = new TextLayout(cont, g.getFont(), g2d.getFontRenderContext());
		Shape s = tl.getOutline(null);
		FontMetrics fm = g.getFontMetrics();
		int conW = fm.stringWidth(cont);
		int conH = fm.getHeight();
		int conX = X + (W - conW) / 2;
		int conY = Y + (H - conH) / 2 + fm.getAscent() - PAD;
		AffineTransform transform = AffineTransform.getTranslateInstance(conX, conY);
		g2d.transform(transform);

		// draw black
		g2d.setStroke(new BasicStroke(5f));
		g2d.setColor(new Color(5, 5, 5));
		g2d.draw(s);

		// draw white
		g2d.setColor(new Color(224, 224, 224));
		g2d.fill(s);

		// restore the original transform
		g2d.setTransform(new AffineTransform());
	}

	private void drawQuit(Graphics g) {
		g.setFont(CUSTOM_FONT);
		Graphics2D g2d = (Graphics2D) g;
		g.setFont(g.getFont().deriveFont(58f));
		TextLayout tl2 = new TextLayout(quit, g.getFont(), g2d.getFontRenderContext());
		Shape s2 = tl2.getOutline(null);
		FontMetrics fm2 = g.getFontMetrics();
		int quitW = fm2.stringWidth(quit);
		int quitH = fm2.getHeight();
		int quitX = X + (W - quitW) / 2;
		int quitY = Y + (H - quitH) / 2 + fm2.getAscent() + PAD;
		AffineTransform transform2 = AffineTransform.getTranslateInstance(quitX, quitY);
		g2d.transform(transform2);

		// draw black
		g2d.setStroke(new BasicStroke(5f));
		g2d.setColor(new Color(5, 5, 5));
		g2d.draw(s2);

		// draw white
		g2d.setColor(new Color(224, 224, 224));
		g2d.fill(s2);
	}

    public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE -> game.getPlaying().resetGameSavePoint();
			case KeyEvent.VK_P, KeyEvent.VK_ENTER -> game.setGameState(PLAYING);
		}
    }
}
