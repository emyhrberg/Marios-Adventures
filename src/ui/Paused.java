package ui;

import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Pause game-state, draws the overlay
 */
public class Paused extends GameState {

	public static boolean isMuted = false;
	private final PauseButton[] buttons = new PauseButton[3];

    public Paused(Game game) {
		super(game);
		initButtons();
    }

	private void initButtons() {
		// y pos
		final int PADDING 	= (int) (100 * Game.SCALE);
		final int OPTION1 	= (int) (200 * Game.SCALE);
		final int OPTION2 	= OPTION1 + PADDING;
		final int OPTION3 	= OPTION2 + PADDING;

		buttons[0] = new PauseButton(0, OPTION1);
		buttons[1] = new PauseButton(1, OPTION2);
		buttons[2] = new PauseButton(2, OPTION3);
//		buttons[3] = new PauseButton(3, OPTION4);
	}

	// Update stuff

	public void update() {
		for (PauseButton b : buttons)
			b.update();
	}

	// NEW!

	public void draw(Graphics g) {
		for (PauseButton b : buttons)
			b.draw(g);
	}

	private boolean isButtonInsideBounds(MouseEvent e, PauseButton mb) {
		return mb.getButtonBounds().contains(e.getX(), e.getY());
	}

	public void mousePressed(MouseEvent e) {
		for (PauseButton b : buttons)
			if (isButtonInsideBounds(e, b))
				b.setMousePressButton(true);
	}

	public void mouseReleased(MouseEvent e) {
		for (PauseButton b : buttons)
			if (isButtonInsideBounds(e, b) && b.isMousePressButton()) {
				buttonPressed(b);
				break;
			}

		// Reset buttons
		for (PauseButton b : buttons) {
			b.setMouseOverButton(false);
			b.setMousePressButton(false);
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
		{ // QUIT
			game.getPlaying().resetGameSavePoint();
			System.out.println("quit");
		}
	}
}
