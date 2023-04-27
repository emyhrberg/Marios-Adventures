package misc;

import main.Game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class handles mouse press, releases and moves
 * Is used with constructor in gameComponent
 * Extends MouseAdapter for overriding methods
 * Every game state has different keyboard inputs as defined in the switch
 */
public class MouseInput extends MouseAdapter {

    // ====== Variables ======
    private final Game game;

    // ====== Constructor ======
    public MouseInput(Game game) {
        this.game = game;
    }

    public void mousePressed(final MouseEvent e) {
        switch (game.getGameState()) {
            case MENU       -> game.getMenu().updateMousePress(e);
            case PLAYING    -> game.getPlaying().mousePressed(e);
        }
    }

    public void mouseReleased(final MouseEvent e) {
        game.getMenu().updateMouseReleased(e);
    }

    public void mouseMoved(MouseEvent e) {
        game.getMenu().setMouseOverButton(e);
    }
}
