package main;

import constants.Direction;

import static constants.Direction.DOWN;
import static constants.Direction.UP;
import static main.EnemyManager.PLANT_HEIGHT;
import static main.EnemyManager.PLANT_WIDTH;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;

public class Plant extends Enemy {

    private static final float PLANT_SPEED = 0.2f * SCALE;
    private static final float PLANT_MAX_DISTANCE = 1.2f;
    private Direction plantDirection = UP;

    public Plant(float x, float y) {
        super(x, y, PLANT_WIDTH, PLANT_HEIGHT);
        initAttackBox(PLANT_WIDTH, PLANT_HEIGHT);
    }

    public void update(Player player) {
        updatePlantPos();
        updatePlantAttacking(player);
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
            if (animationIndex >= 2) {
                animationIndex = 0;
            }
        }
    }

    private void updatePlantPos() {
        float origPos = y / TILES_SIZE;
        float currPos = hitbox.y / TILES_SIZE;
        float tileDistance = origPos - currPos;

        // move up and down
        if (plantDirection == UP) {
            final float increase = 0.0001f;
            final float max = 0.0051f;
            if (tileDistance >= PLANT_MAX_DISTANCE && tileDistance < PLANT_MAX_DISTANCE + max) {
                hitbox.y -= increase;
            } else if (tileDistance >= PLANT_MAX_DISTANCE + max) {
                plantDirection = DOWN;
            } else {
                hitbox.y -= PLANT_SPEED;
            }
        } else if (plantDirection == DOWN) {
            if (tileDistance <= 0 && tileDistance >= -0.1) {
                hitbox.y += 0.01;
            } else if (tileDistance <= -0.1) {
                plantDirection = UP;
            } else {
                hitbox.y += PLANT_SPEED;
            }
        }
    }

    private void updatePlantAttacking(Player player) {
        attackBox.x = hitbox.x - TILES_SIZE / 2f + 3 * SCALE;
        attackBox.y = hitbox.y;

        if (attackBox.intersects(player.getHitbox()) && canDealDamage) {
            player.reducePlayerHealth(20, this);
            canDealDamage = false;
        }
    }
}
