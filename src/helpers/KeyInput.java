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
        game.keyPressed(e);

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
                game.getGameCompletedState().keyPressed(e);
                break;
            case GAME_OVER:
                game.getGameOverState().keyPressed(e);
                break;
            case OPTIONS:
                game.getOptions().keyPressed(e);
            default:
                // Handle the default case here
                break;
        }

    }

    public void keyReleased(KeyEvent e) {
        game.keyReleased(e);
        game.getPlaying().keyReleased(e);
    }

}
