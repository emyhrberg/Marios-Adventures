package entities;

import main.Level;

import static entities.EnemyManager.SHARK_HEIGHT;
import static entities.EnemyManager.SHARK_WIDTH;
import static main.Game.SCALE;

/**
 * The shark class represents a type of Enemy
 * This class is constructed in the Level class where we add sharks to the game
 * The constructor initializes a hitbox and attackbox for the shark
 * The update method updates everything related to the shark, including but not limited to its actions, movement, attackbox, animation.
 */
public class Shark extends Enemy {
    // ====== Hitbox =======
    private static final int HITBOX_WIDTH 		= (int) (24 * SCALE);
    private static final int HITBOX_HEIGHT 		= (int) (31 * SCALE);

    // ====== Attackbox =======
    private static final int ATTACKBOX_WIDTH = HITBOX_WIDTH * 3;
    private static final int ATTACKBOX_HEIGHT = HITBOX_HEIGHT;

    public Shark(float x, float y) {
        super(x, y, SHARK_WIDTH, SHARK_HEIGHT);

        // Initialize shark boxes
        initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        initAttackBox(ATTACKBOX_WIDTH, ATTACKBOX_HEIGHT);
    }

    public void update(Level level, Player player) {
        updateEnemyActions(level, player);
        updateEnemyAttackBox();
        updateAnimationTick();
    }
}
