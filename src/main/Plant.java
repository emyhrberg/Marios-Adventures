package main;

import constants.Direction;
import constants.EnemyConstants;

import static constants.Direction.DOWN;
import static constants.Direction.UP;
import static constants.EnemyConstants.EnemyAction.RUNNING;
import static constants.EnemyConstants.getSpriteAmount;
import static main.EnemyManager.*;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;

public class Plant extends Enemy {

    public Plant(float x, float y) {
        super(x, y, PLANT_WIDTH, PLANT_HEIGHT);

        // Initialize shark boxes
        initAttackBox(PLANT_WIDTH, PLANT_HEIGHT);
        enemyAction = EnemyConstants.EnemyAction.PLANT;
    }

    public void update(Level level, Player player) {
        updatePlantPos(level, player);
        updatePlantAttackbox(player);
        updatePlantAnimationTick();
        updateCollisionCooldown();
    }

    protected void updatePlantAnimationTick() {
        // Update animation tick
        animationTick++;

        // Reset animation tick and update animation index
        if (animationTick >= ANIMATION_SPEED * 6) {
            animationTick = 0;
            animationIndex++;

            // Reset animation index when reached all images
            if (animationIndex >= getSpriteAmount(enemyAction)) {
                animationIndex = 0;

                // Now, we are on the first animation index.
                // Here, we set new enemy actions to prevent from getting stuck in the previous action
                switch (enemyAction) {
                    case ATTACKING, HIT -> enemyAction = RUNNING;
                    case DEAD 		-> enemyAlive = false;
                }
            }
        }
    }

    private static final float PLANT_SPEED = 0.2f * SCALE;
    private static final float PLANT_MAX_DISTANCE = 2;
    private Direction plantDirection = UP;

    private void updatePlantPos(Level level, Player player) {
        float origPos = y / TILES_SIZE;
        float currPos = hitbox.y / TILES_SIZE;
        float tileDistance = origPos - currPos;

        // move up and down
        if (plantDirection == UP) {
            hitbox.y -= PLANT_SPEED;

            if (tileDistance + 0.5 >= PLANT_MAX_DISTANCE) {
                plantDirection = DOWN;
            }
        } else if (plantDirection == DOWN) {
            if (tileDistance <= 0 && tileDistance >= -0.1) {
                hitbox.y += PLANT_SPEED / 20;
                canDealDamage = true;
            } else if (tileDistance <= -0.1) {
                plantDirection = UP;
            } else {
                hitbox.y += PLANT_SPEED;
            }
        }
    }

    private void updatePlantAttackbox(Player player) {
        attackBox.x = hitbox.x - TILES_SIZE / 2f + 3 * SCALE;
        attackBox.y = hitbox.y;

        if (hitbox.intersects(player.getHitbox()) && canDealDamage) {
            player.reducePlayerHealth(1, this);
            canDealDamage = false;
        }
    }
}
