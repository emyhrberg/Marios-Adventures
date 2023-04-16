package constants;


public class PlayerConstants {

    public enum PlayerAction {
		IDLE,
		RUNNING,
		JUMPING,
		FALLING,
		ATTACKING,
		HIT
    }

    /**
     * Returns the number of images to fit the player action
     */
    public static int getSpriteAmount(PlayerAction playerAction) {
		return switch (playerAction) {
			case IDLE 		-> 6;
			case RUNNING 	-> 8;
			case JUMPING	-> 4;
			case FALLING 	-> 2;
			case ATTACKING 	-> 4;
			case HIT 		-> 5;
		};
    }
}
