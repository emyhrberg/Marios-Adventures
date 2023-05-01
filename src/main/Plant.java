package main;

import constants.EnemyConstants;
import constants.EnemyConstants.PlantAction;

import java.util.Random;

import static constants.EnemyConstants.PlantAction.*;
import static ui.Menu.SCALE;
import static ui.Menu.TILES_SIZE;

public class Plant extends Enemy {

    // Position
    protected PlantAction plantAction               = IDLE;
    private static final float PLANT_SPEED          = 0.2f;
    private static final float PLANT_TOP_POS        = 0.75f;
    private static final int PLANT_TOP_WAIT         = 1000;
    private static final int[] PLANT_BOTTOM_WAIT    = {4000,5000,6000};
    private static final Random RND                 = new Random();
    private final float origPos                     = y / TILES_SIZE;
    private long lastTopPosition;
    private long lastBottomPosition;
    private int bottomWaitIndexBetweenZeroAndTwo;

    public Plant(float x, float y) {
        super(x, y, EnemyManager.PLANT_W * EnemyManager.PLANT_SCALE * SCALE, EnemyManager.PLANT_H * EnemyManager.PLANT_SCALE * SCALE);
        float w = EnemyManager.PLANT_W * EnemyManager.PLANT_SCALE * SCALE;
        float h = EnemyManager.PLANT_H * EnemyManager.PLANT_SCALE * SCALE;
        initHitbox(x, y, w, h);
        initAttackbox(x, y, w, h);
    }

    public void update(Player player) {
        updatePlantPos();
        updatePlantAttacking(player);
        updatePlantAnimationTick();
    }

    private void updatePlantPos() {
        float currPos = hitbox.y / TILES_SIZE;
        float tileDistance = origPos - currPos;

        boolean canMoveDown = System.currentTimeMillis() >= lastTopPosition + PLANT_TOP_WAIT;
        boolean canMoveUp = System.currentTimeMillis() >= lastBottomPosition + PLANT_BOTTOM_WAIT[bottomWaitIndexBetweenZeroAndTwo];

        // Movement
        if (plantAction == MOVING_UP_FIRST || plantAction == MOVING_UP_ANIMATE) {
            // Move plant upwards
            hitbox.y -= PLANT_SPEED * SCALE;

            // Start animating plant when starting to appear
            if (tileDistance >= PLANT_TOP_POS * SCALE / 3) {
                plantAction = MOVING_UP_ANIMATE;
            }

            // Plant is at maximum up position, set to top
            if (tileDistance > PLANT_TOP_POS * SCALE) {
                plantAction = TOP;
                lastTopPosition = System.currentTimeMillis();
            }
        } else if (plantAction == TOP) {
            // Idle for some seconds then start moving down
            if (canMoveDown) {
                plantAction = MOVING_DOWN;
            }
        } else if (plantAction == MOVING_DOWN) {
            hitbox.y += PLANT_SPEED * SCALE;
            if (tileDistance <= 0) {
                // Save last bottom position and generate a random number to determine seconds to stay at bottom
                plantAction = IDLE;
                lastBottomPosition = System.currentTimeMillis();
                bottomWaitIndexBetweenZeroAndTwo = RND.nextInt(2);
            }
        } else {
            plantAction = IDLE;
            // Idle for some seconds then start moving up again
            if (canMoveUp) {
                plantAction = MOVING_UP_FIRST;
            }
        }
    }

    private void updatePlantAttacking(Player player) {
        attackbox.x = hitbox.x - TILES_SIZE / 2f + 3 * SCALE;
        attackbox.y = hitbox.y;

        if (!player.isHit() && attackbox.intersects(player.getHitbox())) {
            player.hitByEnemy(this);
        }
    }

    private void updatePlantAnimationTick() {
        // Update animation tick
        animationTick++;

        // Reset animation tick and update animation index
        if (animationTick >= ANIMATION_SPEED * 6) {
            animationTick = 0;
            animationIndex++;

            // Reset animation index when reached all images
            if (animationIndex >= EnemyConstants.getPlantSpriteAmount(plantAction)) {
                animationIndex = 0;
            }
        }
    }
}
