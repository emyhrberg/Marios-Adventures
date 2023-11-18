package helpers;

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
            case MENU:
                game.getMenu().mouseClicked(e);
                break;
            case PLAYING:
                game.getPlaying().mousePressed(e);
                break;
            case PAUSED:
                game.getPauseState().mousePressed(e);
                break;
            case GAME_OVER:
                game.getGameOverState().mousePressed(e);
                break;
            case VOLUME:
                game.getVolume().mousePressed(e);
                break;
            case CONTROLS:
                game.getControls().mousePressed(e);
            default:
                break;
        }
    }

    public void mouseReleased(final MouseEvent e) {
        switch (game.getGameState()) {
            case MENU:
                game.getMenu().mouseReleased(e);
                break;
            case PAUSED:
                game.getPauseState().mouseReleased(e);
                break;
            case VOLUME:
                game.getVolume().mouseReleased();
                break;
            case CONTROLS:
                game.getControls().mousePressed(e);
                break;
            default:
                break;
        }
    }
}
