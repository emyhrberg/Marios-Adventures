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
public class KeyboardInput extends KeyAdapter {

    // ====== Variables ======
    private final Game game;

    // ====== Constructor ======
    public KeyboardInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent e) {
        switch (game.getGameState()) {
            case PLAYING            -> game.getPlaying().keyPressed(e);
            case MENU               -> game.getMenu().keyPressed(e);
            case PAUSED             -> game.getPauseState().keyPressed(e);
            case LEVEL_COMPLETED    -> game.getLevelCompletedState().keyPressed(e);
            case GAME_COMPLETED     -> game.getGameCompletedState().keyPressed(e);
            case GAME_OVER          -> game.getGameOverState().keyPressed(e);
        }
    }

    public void keyReleased(KeyEvent e) {
        game.getPlaying().keyReleased(e);
    }

}
