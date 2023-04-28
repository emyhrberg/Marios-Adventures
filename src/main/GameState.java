package main;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.*;

/**
 * Superclass for all game-states
 * Constructs a state with a game
 * The base class for all game states
 * A game state represents a part of the game, like menu or playing
 * Each state uses the game instance to determine its own behavior
 */
public class GameState {

    // ====== Variables ======
    protected Game game;

    // ====== Constructor ======
    protected GameState(Game game) {
	    this.game = game;
    }

}
