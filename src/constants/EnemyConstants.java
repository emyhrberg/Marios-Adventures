package constants;

/**
 * Constants and enums for all enemies
 */
public class EnemyConstants {
    /**
     * Determines the action of the enemy
     */
    public enum EnemyAction {
		RUNNING,
		ATTACKING,
		HIT,
		DEAD,
		PLANT
	}

    /**
     * Returns the number of images to fit the player action
     */
    public static int getSpriteAmount(EnemyAction enemyAction) {
		return switch (enemyAction) {
			case RUNNING 	-> 6;
			case ATTACKING 	-> 7;
			case HIT, DEAD 	-> 5;
			case PLANT		-> 2;
		};
    }
}
