package ui;

import main.Game;
import main.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

import static ui.Menu.GAME_HEIGHT;
import static ui.Menu.GAME_WIDTH;

/**
 * When in level completed state, this class handles drawing a level completed image
 */
public class LevelCompleted extends GameState {

    // ====== Constructor ======
    public LevelCompleted(Game game) {
	    super(game);
    }

    public void drawLevelCompleted(Graphics g) {
        String s = "Level completed! Press Enter to continue";
        g.setFont(g.getFont().deriveFont(80f));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(s);
        int h = fm.getHeight();
        int x = GAME_WIDTH / 2 - w / 2;
        int y = GAME_HEIGHT / 2 - h / 2;
        g.drawString(s, x, y);
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
