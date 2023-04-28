package constants;

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
		return switch (playerAction) {
			case IDLE 		-> 6;
			case RUNNING 	-> 8;
			case JUMPING	-> 4;
			case FALLING 	-> 2;
			case ATTACKING 	-> 4;
			case HIT 		-> 7;
			case DYING		-> 5;
		};
    }
}
