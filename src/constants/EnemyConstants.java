package constants;

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
		return switch (plantAction) {
			case IDLE -> 1;
			case MOVING_DOWN -> 1;
			case MOVING_UP_FIRST -> 1;
			case MOVING_UP_ANIMATE -> 2;
			case TOP -> 2;
		};
	}

    public static int getSharkSpriteAmount(SharkAction sharkAction) {
		return switch (sharkAction) {
			case RUNNING 	-> 6;
			case ATTACKING 	-> 7;
			case HIT, DEAD 	-> 5;
		};
    }
}
