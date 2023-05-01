package ui;

import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * When in level completed state, this class handles drawing a level completed image
 */
public class LevelCompleted extends GameState {

    // ====== Constructor ======
    public LevelCompleted(Game game) {
	    super(game);
    }

    public void drawLevelCompleted(Graphics g) {
        g.setFont(g.getFont().deriveFont(80f));
        g.drawString("level completed press enter!", 500, 500);
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE :
                game.getPlaying().resetGameGoToMenu();
                break;
            case KeyEvent.VK_ENTER :
                game.getPlaying().resetGameLoadNextLevel();
                break;
        }
    }
}
