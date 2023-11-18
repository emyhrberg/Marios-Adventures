package helpers;

import main.Game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static constants.GameState.VOLUME;

/**
 * This class handles mouse motion for drag/drop, hovering, etc
 */
public class MouseMotionInput extends MouseMotionAdapter {

    // ====== Variables ======
    private final Game game;

    // ====== Constructor ======
    public MouseMotionInput(Game game) {
        this.game = game;
    }

    public void mouseDragged(MouseEvent e) {
        if (game.getGameState() == VOLUME) {
            game.getVolume().mouseDragged(e);
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (game.getGameState() == null) {
            System.err.println("Error: gamestate is null in mouseMoved...:)");
            return;
        }

        switch (game.getGameState()) {
            case MENU:
                game.getMenu().mouseMoved(e);
                break;
            case PAUSED:
                game.getPauseState().mouseMoved(e);
                break;
            default:
                break;
        }
    }
}
