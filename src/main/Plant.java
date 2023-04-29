package main;

import constants.EnemyConstants;
import constants.EnemyConstants.PlantAction;

import java.util.Random;

import static constants.EnemyConstants.PlantAction.*;
import static main.EnemyManager.PLANT_HEIGHT;
import static main.EnemyManager.PLANT_WIDTH;
import static main.Game.TILES_SIZE;

public class Plant extends Enemy {

    // Position
    protected PlantAction plantAction               = IDLE;
    private static final float PLANT_SPEED          = 0.2f * Game.SCALE;
    private static final float PLANT_TOP_POS        = 0.85f * Game.SCALE;
    private static final int PLANT_TOP_WAIT         = 1000;
    private static final int[] PLANT_BOTTOM_WAIT    = {4000,5000,6000};
    private static final Random RND                 = new Random();
    private final float origPos                     = y / TILES_SIZE;
    private long lastTopPosition;
    private long lastBottomPosition;
    private int bottomWaitIndexBetweenZeroAndTwo;

    // Attacking
    private long lastAttack;
    private static final int ATTACK_COOLDOWN = 1000;

    public Plant(float x, float y) {
        super(x, y, PLANT_WIDTH, PLANT_HEIGHT);
        initHitbox(x, y, PLANT_WIDTH, PLANT_HEIGHT);
        initAttackBox(x, y, PLANT_WIDTH, PLANT_HEIGHT);
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
            hitbox.y -= PLANT_SPEED;

            // Start animating plant when starting to appear
            if (tileDistance >= PLANT_TOP_POS / 2) {
                plantAction = MOVING_UP_ANIMATE;
            }

            // Plant is at maximum up position, set to top
            if (tileDistance > PLANT_TOP_POS) {
                plantAction = TOP;
                lastTopPosition = System.currentTimeMillis();
            }
        } else if (plantAction == TOP) {
            // Idle for some seconds then start moving down
            if (canMoveDown) {
                plantAction = MOVING_DOWN;
            }
        } else if (plantAction == MOVING_DOWN) {
            hitbox.y += PLANT_SPEED;
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
        boolean canPlantDealDamage = System.currentTimeMillis() >= lastAttack + ATTACK_COOLDOWN;

        attackBox.x = hitbox.x - TILES_SIZE / 2f + 3 * Game.SCALE;
        attackBox.y = hitbox.y;

        if (attackBox.intersects(player.getHitbox()) && canPlantDealDamage) {
            player.hitByEnemy(this);
            lastAttack = System.currentTimeMillis();
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
