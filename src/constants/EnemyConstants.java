package constants;

/**
 * Actions and number of sprites for all enemies
 */
public class EnemyConstants {

    public enum SharkAction {
		RUNNING,
		ATTACKING,
		HIT,
		DEAD,
	}

	public enum PlantAction {
		IDLE,
		MOVING_DOWN,
		MOVING_UP_FIRST,
		MOVING_UP_ANIMATE,
		TOP
	}

	public static int getPlantSpriteAmount(PlantAction plantAction) {
		int result;
		switch (plantAction) {
			case IDLE:
			case MOVING_DOWN:
			case MOVING_UP_FIRST:
				result = 1;
				break;
			case MOVING_UP_ANIMATE:
			case TOP:
				result = 2;
				break;
			default:
				result = 0;
				break;
		}
		return result;
	}

	public static int getSharkSpriteAmount(SharkAction sharkAction) {
		int result;
		switch (sharkAction) {
			case RUNNING:
				result = 6;
				break;
			case ATTACKING:
				result = 7;
				break;
			case HIT:
			case DEAD:
				result = 5;
				break;
			default:
				// Handle the default case here
				// Set a default value for 'result' if needed
				result = 0;
				break;
		}
		return result;
	}

}
