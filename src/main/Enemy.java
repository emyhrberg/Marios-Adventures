package main;

import constants.Direction;
import helpers.SoundLoader;

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

    protected void updatePlantAnimationTick() {
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
