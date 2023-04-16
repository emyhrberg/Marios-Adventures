package entities;

import constants.Direction;
import helpers.SoundLoader;
import main.Game;
import main.Level;

import java.awt.geom.Rectangle2D;

import static constants.Direction.*;
import static constants.EnemyConstants.EnemyAction;
import static constants.EnemyConstants.EnemyAction.*;
import static constants.EnemyConstants.getSpriteAmount;
import static main.Game.SCALE;
import static main.Game.UPS;

/**
 * This class handles all Enemy behavior, functionality, movement and extends the Entity class
 */
public class Enemy extends Entity {

    // ====== Animations ======
    protected int animationTick, animationIndex;
    protected static final int ANIMATION_SPEED = 15;

    // ====== Enemy Constants ======
    protected static final int HEALTH 	= 1;
    protected static final int DAMAGE	= 20;
    protected static final float SPEED	= 0.25f * Game.SCALE;

    // ====== Enemy Values ======
    protected EnemyAction enemyAction = RUNNING;
    protected boolean enemyAlive = true;

	// ====== Push back, Cooldown collision =====
	protected float pushDrawOffset;
	protected Direction pushBackOffsetDir = UP;
	protected int coolDownTime = UPS; // 1 second
	protected int cooldownTimer;
	protected boolean canDealDamage = true;
	private long previousTime = System.currentTimeMillis();

    // ====== Enemy Detection ======
    protected static final float DETECT_DISTANCE = Game.TILES_SIZE * 3;
    protected static final float ATTACK_DISTANCE = (float) (Game.TILES_SIZE);

    // ====== Constructor ======
    protected Enemy(float x, float y, float width, float height) {
		super(x, y, width, height);

		// Init enemy values
		setDirection(LEFT);
		initSpeed(SPEED);
		initMaxHealth(HEALTH);
		initHitbox(x, y, width, height);
    }

    protected void updateEnemyActions(Level level, Player player) {
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

	protected void updateEnemyAttackBox() {
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

    // ====== Update enemy values ======

    protected void reduceEnemyHealth(Player player) {
		health -= 1;

		// Update push back direction
		if (player.getHitbox().x < hitbox.x)
			pushBackDirection = RIGHT;
		else
			pushBackDirection = LEFT;

		// Set enemy action
		if (health <= 0)
			setAction(DEAD);
		else
			setAction(HIT);

		// Play sound
		SoundLoader.playAudio("enemy_taking_damage.wav", 0.2);
    }

    protected void dealDamageToPlayer(Enemy enemy, Player player) {
		if (enemy.attackBox.intersects(player.hitbox)) {
			player.reducePlayerHealth(DAMAGE, this);
		}
		attackChecked = true;
    }

    public void resetEnemy() {
		setAction(RUNNING);
		hitbox.x = x;
		hitbox.y = y;
		health = maxHealth;
		enemyAlive = true;
    }

	protected void checkCollisionWithPlayer(Player player) {
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

	protected void updateCollisionCooldown() {
		long currentTime = System.currentTimeMillis();
		int elapsedUpdates = (int) ((currentTime - previousTime) / (1000.0f / UPS));
		previousTime = currentTime;
		cooldownTimer += elapsedUpdates;
		if (cooldownTimer > coolDownTime) {
			canDealDamage = true;
			cooldownTimer = 0;
		}
	}

	// ====== Animations ======

    protected void updateAnimationTick() {
		// Update animation tick
		animationTick++;

		// Reset animation tick and update animation index
		if (animationTick >= ANIMATION_SPEED) {
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

    // ====== Setters ======

    protected void setAction(EnemyAction enemyAction) {
		this.enemyAction = enemyAction;

		// When setting a new action, reset the previous action
		animationTick = 0;
		animationIndex = 0;
    }

    // ====== Getters ======

	public float getPushDrawOffset() {
		return pushDrawOffset;
	}

	public int getImageFlipX() {
		if (direction == RIGHT)
			return (int) (width);
		else
			return 0;
    }

    public int getImageFlipWidth() {
		if (direction == RIGHT)
			return -1;
		else
			return 1;
    }

    public int getAnimationIndex() {
		return animationIndex;
    }

    public EnemyAction getEnemyAction() {
		return enemyAction;
    }

    public boolean isEnemyAlive() {
		if (hitbox.y >= Game.GAME_HEIGHT)
			return false;
		return enemyAlive;
	}
}
