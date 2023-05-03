package ui;

import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static constants.GameState.*;
import static ui.Menu.GAME_HEIGHT;
import static ui.PauseButton.BUTTON_H;

/**
 * Pause game-state, draws the overlay
 */
public class Paused extends GameState {

	private final PauseButton[] buttons = new PauseButton[3];

    public Paused(Game game) {
		super(game);
		initButtons();
    }

	private void initButtons() {
		final int PADDING 	= (int) (BUTTON_H * 0.75f);
		final int CENTER 	= GAME_HEIGHT / 2 - BUTTON_H / 2;

		buttons[0] = new PauseButton(0, CENTER - PADDING);
		buttons[1] = new PauseButton(1, CENTER);
		buttons[2] = new PauseButton(2, CENTER + PADDING);
	}

	public void draw(Graphics g) {
		for (PauseButton b : buttons)
			b.draw(g);
	}

	public void update() {
		for (PauseButton b : buttons)
			b.update();
	}

	private void buttonPressed(PauseButton b) {
		// PLAY
		if (b.getButtonIndex() == 0)
			game.setGameState(PLAYING);

		// SETTINGS
		if (b.getButtonIndex() == 1)
			game.setGameState(OPTIONS);

		// SAVE QUIT
		if (b.getButtonIndex() == 2) {
			game.getPlaying().saveSpawnPointGoToMenu();
		}
	}

	private boolean isButtonInsideBounds(MouseEvent e, PauseButton b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {
		for (PauseButton b : buttons)
			if (isButtonInsideBounds(e, b))
				b.setMousePressButton(true);
	}

	public void mouseReleased(MouseEvent e) {
		for (PauseButton b : buttons)
			if (isButtonInsideBounds(e, b) && b.isMousePressButton())
				buttonPressed(b);

		for (PauseButton button : buttons) {
			button.setMouseOverButton(false);
			button.setMousePressButton(false);
		}
	}

	public void mouseMoved(MouseEvent e) {
		for (PauseButton button : buttons)
			button.setMouseOverButton(isButtonInsideBounds(e, button));
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_ESCAPE:
				game.setGameState(MENU);
				break;
			case KeyEvent.VK_P:
			case KeyEvent.VK_ENTER:
				game.setGameState(PLAYING);
				break;
		}
	}
}
