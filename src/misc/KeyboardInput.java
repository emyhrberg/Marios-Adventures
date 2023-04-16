package misc;

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

    @Override public void keyPressed(KeyEvent e) {
        switch (game.getGameState()) {
            case PLAYING            -> game.getPlaying().keyPressed(e);
            case MENU               -> game.getMenu().keyPressed(e);
            case PAUSED             -> game.getPauseOverlay().keyPressed(e);
            case LEVEL_COMPLETED    -> game.getLevelCompletedOverlay().keyPressed(e);
            case GAME_COMPLETED     -> game.getGameCompletedOverlay().keyPressed();
            case GAME_OVER          -> game.getGameOverOverlay().keyPressed(e);
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        game.getPlaying().keyReleased(e);
    }

}
