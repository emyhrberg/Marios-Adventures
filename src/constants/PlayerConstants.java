package constants;

/**
 * Constants for the player action and the number of animation for each respective action
 */
public class PlayerConstants {

    public enum PlayerAction {
		IDLE,
		RUNNING,
		JUMPING,
		FALLING,
		ATTACKING,
		HIT,
		DYING
    }
	
    public static int getSpriteAmount(PlayerAction playerAction) {
		int result = 1;
		switch (playerAction) {
			case IDLE:
				result = 6;
				break;
			case RUNNING:
				result = 8;
				break;
			case JUMPING:
				result = 4;
				break;
			case FALLING:
				result = 2;
				break;
			case ATTACKING:
				result = 4;
				break;
			case HIT:
				result = 7;
				break;
			case DYING:
				result = 5;
				break;
			default:
				// Handle the default case here
				// Set a default value for 'result' if needed
				break;
		}
		return result;
	}
}
