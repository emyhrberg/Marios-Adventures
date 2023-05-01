package ui;

import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static ui.Menu.SCALE;

/**
 * Pause game-state, draws the overlay
 */
public class Paused extends GameState {

	public static boolean isMuted = false;
	private final PauseButton[] buttons = new PauseButton[4];

    public Paused(Game game) {
		super(game);
		initButtons();
    }

	private void initButtons() {
		// y pos
		final int PADDING 	= (int) (100 * SCALE);
		final int OPTION1 	= (int) (200 * SCALE);

		buttons[0] = new PauseButton(0, OPTION1);
		buttons[1] = new PauseButton(1, OPTION1 + PADDING);
		buttons[2] = new PauseButton(2, OPTION1 + PADDING * 2);
		buttons[3] = new PauseButton(3, OPTION1 + PADDING * 3);
	}

	public void draw(Graphics g) {
		for (PauseButton b : buttons)
			b.draw(g);
	}

	public void update() {
		for (PauseButton b : buttons)
			b.update();
	}

	private boolean isButtonInsideBounds(MouseEvent e, PauseButton b) {
		return b.getButtonBounds().contains(e.getX(), e.getY());
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
				game.setGameState(constants.GameState.MENU);
				break;
			case KeyEvent.VK_P:
			case KeyEvent.VK_ENTER:
				game.setGameState(constants.GameState.PLAYING);
				break;
		}
	}

	private void buttonPressed(PauseButton b) {
		if (b.getButtonIndex() == 0)
		{ // RESUME
			game.setGameState(constants.GameState.PLAYING);
		}
		else if (b.getButtonIndex() == 1)
		{ // MUTE & UNMUTE
			isMuted = !isMuted;
		}
		else if (b.getButtonIndex() == 2)
		{ // SAVE
			game.getPlaying().saveSpawnPoint();
		}
		else if (b.getButtonIndex() == 3)
		{ // QUIT
			game.getPlaying().resetGameGoToMenu();
		}
	}
}
