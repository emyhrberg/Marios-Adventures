package main;

import constants.Direction;
import constants.EnemyConstants;
import helpers.SoundLoader;

import java.awt.geom.Rectangle2D;

import static constants.Direction.LEFT;
import static constants.Direction.RIGHT;
import static constants.EnemyConstants.SharkAction.ATTACKING;
import static constants.EnemyConstants.SharkAction.RUNNING;
import static constants.EnemyConstants.getSharkSpriteAmount;
import static main.EnemyManager.*;

/**
 * The shark class represents a type of Enemy
 * This class is constructed in the Level class where we add sharks to the game
 * The constructor initializes a hitbox and attackbox for the shark
 * The update method updates everything related to the shark, including but not limited to its actions, movement, attackbox, animation.
 */
public class Shark extends Enemy {

    // ====== Hitboxes =======
    private static final int HITBOX_WIDTH 		    = 24;
    private static final int HITBOX_HEIGHT 		    = 31;
    private static final int ATTACKBOX_WIDTH        = HITBOX_WIDTH * 3;
    private static final int ATTACKBOX_HEIGHT       = HITBOX_HEIGHT;

    // ====== Enemy Settings ======
    protected boolean enemyAlive = true;
    protected EnemyConstants.SharkAction sharkAction = EnemyConstants.SharkAction.RUNNING;
    protected static final int MAX_HEALTH           = 1;
    protected static final float SPEED	            = 0.25f;
    protected static final float DETECT_DISTANCE    = Game.TILES_SIZE * 3;
    protected static final float ATTACK_DISTANCE    = (float) (Game.TILES_SIZE);

    public Shark(float x, float y) {
        super(x, y, EnemyManager.SHARK_W * SHARK_SCALE * Game.SCALE, EnemyManager.SHARK_H * SHARK_SCALE * Game.SCALE);

        // Init hitboxes
        initHitbox(x, y, HITBOX_WIDTH * Game.SCALE, HITBOX_HEIGHT * Game.SCALE);
        initAttackbox(x, y, ATTACKBOX_WIDTH, ATTACKBOX_HEIGHT);

        // Init enemy settings
        initSpeed(SPEED * Game.SCALE);
        initMaxHealth(MAX_HEALTH);
    }

    public void update(Level level, Player player) {
        updateSharkActions(level, player);
        updateAttackbox();
        updateSharkAnimationTick();
    }

    protected void updateSharkActions(Level level, Player player) {
        switch (sharkAction) {
            case RUNNING:
                updatePlayerSharkCollision(player);
                updateRunning(level, player);
                break;
            case ATTACKING:
                updatePlayerSharkCollision(player);
                updateAttacking(player);
                break;
            case HIT:
            case DEAD:
                pushEnemy(level);
                break;
            default:
                // Handle the default case here
                break;
        }
    }

    private void updateRunning(Level level, Player player) {
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

    // ====== Shark Patrol ======

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

    private void updateDirection() {
        if (direction == LEFT)
            xDirection = -xSpeed;
        else if (direction == RIGHT)
            xDirection = xSpeed;
        else
            xDirection = 0;
    }

    private boolean isSolidGround(Rectangle2D.Float hitbox, float xDirection, Level level) {
        if (xDirection > 0) // moving right: offset x by width
            return isSolid(hitbox.x + hitbox.width + xDirection, hitbox.y + hitbox.height + 1, level);
        else // moving left: no x offset by width
            return isSolid(hitbox.x + xDirection, hitbox.y + hitbox.height + 1, level);
    }

    // ====== Shark Detection ======

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

    // ====== Shark Attacking ======

    private void updateAttacking(Player player) {
        // Do not attack on the first animation index
        if (animationIndex == 0)
            attackChecked = false;

        // Only deal damage on the last animation index
        final int attackIndex = 4;
        if (animationIndex == attackIndex && !player.isHit() && attackbox.intersects(player.hitbox))
            player.hitByEnemy(this);
    }

    private static final int DISALLOW_COLLISION = 200;

    private void updatePlayerSharkCollision(Player player) {
        player.setCanCollide(System.currentTimeMillis() - player.getLastCheck() >= player.disallowCollision());

        if (hitbox.intersects(player.getHitbox()) && player.canCollide()) {
            float playerHitbox = player.hitbox.y + player.hitbox.height;
            float enemyHitbox = hitbox.y + hitbox.height;
            float distBetweenPlayerAndEnemy = Math.abs(playerHitbox - enemyHitbox);
            float enemyHead = hitbox.height - 10 * Game.SCALE;
            boolean isTouchingEnemyHead = distBetweenPlayerAndEnemy > enemyHead;

            // usually distance is slightly above 42 when landing on top of the enemy, also check that player is falling downwards
            if (isTouchingEnemyHead && player.airSpeed > 0) {
                reduceEnemyHealth(player);
                player.jumpOnEnemy();
                SoundLoader.playSound("/sounds/stomp.wav", 0.8);
            } else if (!player.hit && attackbox.intersects(player.hitbox)) {
                player.hitByEnemy(this);
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

    // ====== Shark Misc ======

    private void pushEnemy(Level level) {
        // Set X Direction
        if (pushXDir == LEFT)
            xDirection = -xSpeed;
        if (pushXDir == RIGHT)
            xDirection = xSpeed;

        // Push back X with double speed!
        moveToPosition(hitbox.x + xDirection * 2, hitbox.y, hitbox.width, hitbox.height, level);

    }

    // ====== Update enemy values ======

    protected void reduceEnemyHealth(Player player) {
        health -= 1;

        // Update push dir
        if (player.getHitbox().x < hitbox.x)
            pushXDir = Direction.RIGHT;
        else
            pushXDir = Direction.LEFT;

        // Set enemy action
        if (health <= 0)
            setAction(EnemyConstants.SharkAction.DEAD);
        else
            setAction(EnemyConstants.SharkAction.HIT);
    }

    public void resetEnemy() {
        setAction(EnemyConstants.SharkAction.RUNNING);
        hitbox.x = x;
        hitbox.y = y;
        health = maxHealth;
        enemyAlive = true;
    }

    // ====== Getters && Setters ======

    protected void setAction(EnemyConstants.SharkAction sharkAction) {
        this.sharkAction = sharkAction;

        // When setting a new action, reset the previous action
        animationTick = 0;
        animationIndex = 0;
    }

    public float getImageFlipX() {
        if (direction == Direction.RIGHT)
            return width;
        else
            return 0;
    }

    public int getImageFlipWidth() {
        if (direction == Direction.RIGHT)
            return -1;
        else
            return 1;
    }

    public boolean isEnemyAlive() {
        if (hitbox.y >= Game.GAME_HEIGHT)
            return false;
        return enemyAlive;
    }

    // ====== Animations ======

    private void updateSharkAnimationTick() {
        // Update animation tick
        animationTick++;

        // Reset animation tick and update animation index
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            animationIndex++;

            // Reset animation index when reached all images
            if (animationIndex >= getSharkSpriteAmount(sharkAction)) {
                animationIndex = 0;

                // Now, we are on the first animation index.
                // Here, we set new enemy actions to prevent from getting stuck in the previous action
                switch (sharkAction) {
                    case ATTACKING:
                    case HIT:
                        sharkAction = RUNNING;
                        break;
                    case DEAD:
                        enemyAlive = false;
                        break;
                }
            }
        }
    }
}
