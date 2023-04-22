package main;

import java.awt.geom.Rectangle2D;

import static constants.Direction.*;
import static constants.EnemyConstants.EnemyAction.ATTACKING;
import static main.EnemyManager.SHARK_HEIGHT;
import static main.EnemyManager.SHARK_WIDTH;
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
        initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        initAttackBox(ATTACKBOX_WIDTH, ATTACKBOX_HEIGHT);
    }

    public void update(Level level, Player player) {
        updateSharkActions(level, player);
        updateEnemyAttackbox();
        updatePlantAnimationTick();
    }

    protected void updateSharkActions(Level level, Player player) {
        switch (enemyAction) {
            case RUNNING 	-> updateRunning(level, player);
            case ATTACKING 	-> updateAttacking(player);
            case HIT, DEAD	-> updatePushback(level);
        }
    }

    private void updateRunning(Level level, Player player) {
        updateCollisionCooldown();
        checkCollisionWithPlayer(player);

        // ====== Start falling on spawn ======
        if (isEntityInAir(hitbox, level))
            inAir = true;
        if (inAir)
            startFalling(level);

        // ====== Update X Direction ======
        updateDirection();
        updateEnemyPatrol(level);
        updateEnemyDetection(level, player);
    }

    // ====== Enemy Patrol ======

    private void updateEnemyPatrol(Level level) {
        // Update the enemy hitbox if not colliding with a solid block on the next X position
        if (canMoveToPosition(hitbox.x + xDirection, hitbox.y, hitbox.width, hitbox.height, level))
            if (isSolidGround(hitbox, xDirection, level)) { // make sure enemy doesn't keep moving after he hits edge
                hitbox.x += xDirection; // update enemy x position
                return;
            }

        if (isEntityInAir(hitbox, level)) {
            direction = LEFT;
            return;
        }

        // Enemy has reached an edge of his patrol, change direction!
        if (direction == LEFT)
            direction = RIGHT;
        else if (direction == RIGHT)
            direction = LEFT;
    }

    private boolean isSolidGround(Rectangle2D.Float hitbox, float xDirection, Level level) {
        if (xDirection > 0) // moving right: offset x by width
            return isSolid(hitbox.x + hitbox.width + xDirection, hitbox.y + hitbox.height + 1, level);
        else // moving left: no x offset by width
            return isSolid(hitbox.x + xDirection, hitbox.y + hitbox.height + 1, level);
    }

    // ====== Enemy Detection ======

    private void updateEnemyDetection(Level level, Player player) {
        // Get Y position of the player and enemy
        int enemyY 	= (int) (hitbox.y / Game.TILES_SIZE);
        int playerY 	= (int) (player.getHitbox().y / Game.TILES_SIZE);

        // Get distance between player and enemy hitbox
        int distance = (int) Math.abs(player.hitbox.x - hitbox.x);

        // Check if within detect distance, on same Y, and not blocked by tiles
        if (distance <= DETECT_DISTANCE && enemyY == playerY && isSightClear(level, player)) {
            // All criteria filled, player detected!
            turnTowardsPlayer(player);

            if (canAttackPlayer(player))
                setAction(ATTACKING);
        }
    }

    private boolean isSightClear(Level level, Player player) {
        Rectangle2D.Float playerBox = player.getHitbox();
        int tileY = (int) (hitbox.y / Game.TILES_SIZE);
        int xStart = (int) (hitbox.x / Game.TILES_SIZE);
        int xEnd;

        // check when player is on the left edge
        if (isSolid(playerBox.x, playerBox.y + playerBox.height + 1, level))
            xEnd = (int) (playerBox.x / Game.TILES_SIZE);
        else
            xEnd = (int) ((playerBox.x + playerBox.width) / Game.TILES_SIZE);

        if (xStart > xEnd)
            return isAllTilesWalkable(xEnd, xStart, tileY, level);
        else
            return isAllTilesWalkable(xStart, xEnd, tileY, level);
    }

    private boolean isAllTilesWalkable(int xStart, int xEnd, int tileY, Level level) {
        if (IsAllTilesClear(xStart, xEnd, tileY, level))
            for (int i = 0; i < xEnd - xStart; i++) {
                if (!isTileSolid(xStart + i, tileY + 1, level))
                    return false;
            }
        return true;
    }

    private boolean IsAllTilesClear(int xStart, int xEnd, int y, Level level) {
        for (int i = 0; i < xEnd - xStart; i++)
            if (isTileSolid(xStart + i, y, level))
                return false;
        return true;
    }

    // ====== Enemy Attacking ======

    private void updateAttacking(Player player) {
        updateCollisionCooldown();
        checkCollisionWithPlayer(player);

        // Do not attack on the first animation index
        if (animationIndex == 0)
            attackChecked = false;

        // Only deal damage on the last animation index
        final int lastAttackAniIndex = 4;
        if (animationIndex == lastAttackAniIndex && !attackChecked)
            dealDamageToPlayer(this, player);
    }

    private void checkCollisionWithPlayer(Player player) {
        if (hitbox.intersects(player.getHitbox()) && canDealDamage) {

            float playerHitbox = player.hitbox.y + player.hitbox.height;
            float enemyHitbox = hitbox.y + hitbox.height;
            float distBetweenPlayerAndEnemy = Math.abs(playerHitbox - enemyHitbox);
            float enemyHead = hitbox.height - 10 * SCALE;
            boolean isTouchingEnemyHead = distBetweenPlayerAndEnemy > enemyHead;

            // usually distance is slightly above 42 when landing on top of the enemy
            if (isTouchingEnemyHead) {
                reduceEnemyHealth(player);
                player.jumpOnEnemy();
            } else {
                dealDamageToPlayer(this, player);
                canDealDamage = false;
            }
        }
    }

    private void turnTowardsPlayer(Player player) {
        if (hitbox.x < player.hitbox.x)
            direction = RIGHT;
        else
            direction = LEFT;
    }

    private boolean canAttackPlayer(Player player) {
        // Get distance between player and enemy hitbox
        int distance = (int) Math.abs(player.hitbox.x - hitbox.x);

        // If the distance is less than the attackDistance, the enemy should attack the player!
        return distance <= ATTACK_DISTANCE;
    }

    private void updateEnemyAttackbox() {
        // Attackbox X and Y when standing still
        attackBox.y = hitbox.y;
        attackBox.x = hitbox.x - hitbox.width / 2;

        // Attackbox moves when moving left or right
        if (direction == LEFT)
            attackBox.x = hitbox.x + hitbox.width - attackBox.width;
        if (direction == RIGHT)
            attackBox.x = hitbox.x;
    }

    // ====== Enemy pushback =======

    private void updatePushback(Level level) {
        // Set X Direction
        if (pushBackDirection == LEFT)
            xDirection = -xSpeed;
        if (pushBackDirection == RIGHT)
            xDirection = xSpeed;

        // Push back X with double speed!
        moveToPosition(hitbox.x + xDirection * 2, hitbox.y, hitbox.width, hitbox.height, level);

        // Set Y Direction
        float speed = 0.95f;
        float limit = -30f;
        if (pushBackOffsetDir == UP) {
            pushDrawOffset -= speed;
            if (pushDrawOffset <= limit)
                pushBackOffsetDir = DOWN;
        } else {
            pushDrawOffset += speed;
            if (pushDrawOffset >= 0)
                pushDrawOffset = 0;
        }
    }
}
