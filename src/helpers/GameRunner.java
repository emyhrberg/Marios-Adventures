package helpers;

import main.Game;

/**
 * The GameRunner class is responsible for creating a new instance of the Game and launching it
 * The class has a main method which calls the startGameLoop to start the game
 * The class also initializes a logger
 */
public class GameRunner {
    public static void main(String[] args) {
        new Game();
    }
}
