package constants;

/**
 * This enum determines current state of the game
 * The state depends on the actions of the user
 * Examples include: can be inside the menu, or the pause screen or the main playing, or something else
 */
public enum GameState {
    PLAYING,
    MENU,
    QUIT,
    PAUSED,
    LEVEL_COMPLETED,
    GAME_COMPLETED,
    GAME_OVER
}
