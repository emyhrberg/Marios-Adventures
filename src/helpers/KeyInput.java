package helpers;

import main.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * This class handles keyboard press and releases
 * Is used with constructor in gameComponent
 * Extends KeyAdapter for keyPressed and keyReleased methods
 * Every game state has different keyboard inputs as defined in the switch
 */
public class KeyInput extends KeyAdapter {

    // ====== Variables ======
    private final Game game;

    // ====== Constructor ======
    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        switch (game.getGameState()) {
            case PLAYING:
                game.getPlaying().keyPressed(e);
                break;
            case MENU:
                game.getMenu().keyPressed(e);
                break;
            case PAUSED:
                game.getPauseState().keyPressed(e);
                break;
            case LEVEL_COMPLETED:
                game.getLevelCompletedState().keyPressed(e);
                break;
            case GAME_COMPLETED:
                game.getGameCompletedState().keyPressed();
                break;
            case GAME_OVER:
                game.getGameOverState().keyPressed(e);
                break;
            case VOLUME:
                game.getVolume().keyPressed(e);
            case CONTROLS:
                game.getControls().keyPressed(e);
            default:
                break;
        }

    }

    public void keyReleased(KeyEvent e) {
        // TODO : why double call here?
        // one for release to resize screen
        // one for key release when playing for moving A D?
        game.keyReleased(e);
        game.getPlaying().keyReleased(e);
    }

}
