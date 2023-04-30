package main;

import helpers.ImageLoader;
import helpers.SoundLoader;
import objects.Bullet;

import java.awt.*;
import java.awt.image.BufferedImage;

import static constants.Direction.*;
import static constants.GameState.GAME_OVER;
import static constants.PlayerConstants.PlayerAction;
import static constants.PlayerConstants.PlayerAction.*;
import static constants.PlayerConstants.getSpriteAmount;
import static main.Game.UPS;

/**
 * Handles player movement by updating its position
 * Handles player animation and rendering
 * Inherits properties from the Entity class
 */
public class Player extends Entity {

	// ====== Player Animation ======
	private int animationTick, animationIndex;
	private static final int ANIMATION_SPEED 			= 15;
	private static final int ROWS 						= 7;
	private static final int IMAGES_IN_ROW 				= 8;
	private final BufferedImage[][] playerImages 		= new BufferedImage[ROWS][IMAGES_IN_ROW];

	// ====== Player Size ======
	private static final BufferedImage PLAYER_SPRITES 	= ImageLoader.loadImage("/entities/player.png");
	public static final int PLAYER_WIDTH 				= 50;
	public static final int PLAYER_HEIGHT 				= 50;
	private static final int PLAYER_X_OFF 				= (int) (14 * Game.SCALE);
	private static final int PLAYER_Y_OFF 				= (int) (18 * Game.SCALE);
	private static final int HITBOX_WIDTH 				= (int) (30 * Game.SCALE);
	private static final int HITBOX_HEIGHT 				= (int) (39 * Game.SCALE);
	private static final int ATTACKBOX_WIDTH 			= HITBOX_WIDTH * 3;
	private static final int ATTACKBOX_HEIGHT 			= HITBOX_HEIGHT;
	private int imageFlipX, imageFlipWidth = 1;

	// ====== Player Settings ======
	private PlayerAction playerAction 					= IDLE;
	private static final float SPEED					= 0.8f * Game.SCALE;
	private static final int START_HEALTH 				= 30;

	// ====== Jumping ======
	private boolean canJump = true;
	private boolean jumping = false;
	private static final float MAX_JUMP_HEIGHT = 2.5f * Game.SCALE;
	private float jumpHeight = MAX_JUMP_HEIGHT;
	private long lastJumpTime;
	private static final int JUMP_MAX_BOOST_TIME = UPS;
	private boolean holdingSpace;

	// ====== Game variables =======
	private final Game game;
	private Level level;

	// ====== Constructor =======

	public Player(float width, float height, Game game) {
		super(0,0, width, height);
		this.game = game;

		setDirection(STILL);
		initSpeed(SPEED);
		initMaxHealth(START_HEALTH);
		initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
		initAttackBox(x, y, ATTACKBOX_WIDTH, ATTACKBOX_HEIGHT);
		initImages();
	}

	private void initImages() {
		// Loop through all images and populate the animations 2d array with each sub-image
		for (int row = 0; row < ROWS; row++)
			for (int rowIndex = 0; rowIndex < IMAGES_IN_ROW; rowIndex++) {
				int x = rowIndex * PLAYER_WIDTH;
				int y = row * PLAYER_HEIGHT;
				playerImages[row][rowIndex] = PLAYER_SPRITES.getSubimage(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
			}
	}

	// ====== Update =======

	public void update() {
		updatePos();
		updateAttackbox();
		updateAttacking();
		updateAnimationTick();
		updateAnimation();
	}

	private void updatePos() {
		// Check if player is in air and set inAir to true if he is
		if (isEntityInAir(hitbox, level))
			inAir = true;

		// Player is in air; fall to the ground
		if (inAir)
			startFalling(level);

		// Player wants to move horizontally
		updateDirection();
		moveToPosition(hitbox.x + xDirection, hitbox.y, hitbox.width, hitbox.height, level);

		// Jump and boost
		boolean maxJumpBoost = System.currentTimeMillis() <= JUMP_MAX_BOOST_TIME + lastJumpTime;
		if (holdingSpace && airSpeed < 0 && maxJumpBoost) {
			airSpeed -= GRAVITY*1.4;
		}

		if (canJump && jumping) {
			jump();
		}
	}

	public void jump() {
		if (inAir || !canJump)
			return;

		unbindPlatform();

		// start jump
		lastJumpTime = System.currentTimeMillis();
		jumpHeight = MAX_JUMP_HEIGHT;
		inAir = true;
		airSpeed = -jumpHeight;

		// reset jump
		jumping = false;
		canJump = false;

		SoundLoader.playSound("/sounds/jump.wav", 0.5);
	}

	public void jumpOnEnemy() {
		// set jump
		jumpHeight = (float) (1.1 * MAX_JUMP_HEIGHT);
		inAir = true;
		airSpeed = -jumpHeight;

		// reset jump
		jumpHeight = MAX_JUMP_HEIGHT;
		jumping = false;
	}

	private void updateAttacking() {
		if (attacking) {
			// Do not attack on the first animation index
			if (animationIndex == 0)
				attackChecked = false;

			// Only deal damage on the last animation index
			final int attackIndex = 3;
			if (animationIndex == attackIndex && !attackChecked) {
				game.getPlaying().getEnemyManager().attackEnemyIfHit(this);
				attackChecked = true;
			}
		}
	}

	// ====== Public player methods ======

	public void hitByEnemy(Enemy enemy) {
		health -= 1;
		hit = true;

		// Update push direction
		if (enemy.getHitbox().x < hitbox.x)
			pushXDir = RIGHT;
		else
			pushXDir = LEFT;

		// bounce back
		jumpOnEnemy();

		SoundLoader.playSound("/sounds/ouchplayer.wav");
	}

	public void hitByBullet(Bullet b) {
		health -= 1;
		hit = true;

		// set push dir
		if (b.getHitbox().x < hitbox.x)
			pushXDir = RIGHT;
		else
			pushXDir = LEFT;

		// bounce back
		jumpOnEnemy();

		SoundLoader.playSound("/sounds/ouchplayer.wav");
	}

	public void resetPlayer() {
		jumping = false;
		inAir = false;
		attacking = false;
		hit = false;
		health = maxHealth;
		direction = STILL;
		imageFlipWidth = 1;
		imageFlipX = 0;
		setInLava(false);
	}

	// ====== Draw and Animations ======

	public void draw(Graphics g, int levelOffset) {
		// Update player X and width based on the direction to flip the image
		updateImageFlip();

		// Get x, y, width and height in order to draw the animations
		final float x = hitbox.x - levelOffset - PLAYER_X_OFF + imageFlipX;
		final float y = hitbox.y - PLAYER_Y_OFF;
		final float w = width * imageFlipWidth;

		// Get the proper image representing the right action
		final int action = playerAction.ordinal();
		final BufferedImage img = playerImages[action][animationIndex];

		// Draw the image according to the player action and loop through the animation index to show all the images
		g.drawImage(img, (int) x, (int) y, (int) w, (int) height, null);

		if (Game.DEBUG) {
			drawHitbox(g, levelOffset);
			drawAttackBox(g, levelOffset);
		}
	}

	private void updateImageFlip() {
		// When facing right, draw default with X at 0 and width at 1
		if (direction == RIGHT) {
			imageFlipX = 0;
			imageFlipWidth = 1;
		}
		// When facing left, draw from left to right by drawing an extra width and flipping the image width with -1
		if (direction == LEFT) {
			imageFlipX = (int) (width / 1.15);
			imageFlipWidth = -1;
		}
	}

	private void updateAnimationTick() {
		if (playerAction == DYING) {
			return;
		}

		// Update animation tick
		animationTick++;

		// Reset animation tick and update animation index
		if (animationTick >= ANIMATION_SPEED) {
			animationTick = 0;
			animationIndex++;

			// Reset animation index when reached all images
			if (animationIndex >= getSpriteAmount(playerAction)) {
				animationIndex = 0;

				// Reset attacking, jump height and hit after all images have been shown
				attacking = false;
				attackChecked = false;
				hit = false;
			}
		}
	}

	private void updateAnimation() {
		final PlayerAction startAnimation = playerAction;

		if (health <= 0) {
			playerDeathAndGameOver();
		} else if (hit) {
			playerAction = HIT;
			pushPlayer();
		} else if (attacking) {
			playerAction = ATTACKING;
		} else if (inAir && airSpeed < 0) {
			playerAction = JUMPING;
		} else if (inAir && airSpeed > 0) {
			playerAction = FALLING;
		} else if (direction == LEFT || direction == RIGHT) {
			playerAction = RUNNING;
		} else {
			playerAction = IDLE;
		}

		// playerAction changes; reset aniTick and aniIndex to show animation properly
		if (startAnimation != playerAction) {
			animationTick = 0;
			animationIndex = 0;
		}
	}

	private void pushPlayer() {
		// Update xDirection based on pushBackDirection
		if (pushXDir == LEFT)
			xDirection = -xSpeed;
		if (pushXDir == RIGHT)
			xDirection = xSpeed;

		// Move player X, direction with double direction
		moveToPosition((float) (hitbox.x + xDirection * 0.5), hitbox.y, hitbox.width, hitbox.height, level);
	}

	private void playerDeathAndGameOver() {
		if (isEntityInAir(hitbox, level) && !isInLava()) {
			hitbox.y += 1;
		}

		// death animation
		health = 0;
		if (playerAction != DYING) {
			playerAction = DYING;
			animationIndex = 0;
			animationTick = 0;
		}
		animationTick++;
		if (animationTick >= ANIMATION_SPEED) {
			animationTick = 0;
			animationIndex++;
			if (animationIndex >= 4) {
				animationIndex = 4;
				game.setGameState(GAME_OVER);
			}
		}
	}

	// ====== Getters & Setters ======

	public void setHoldingSpace(boolean holdingSpace) {
		this.holdingSpace = holdingSpace;
	}

	public boolean isHit() {
		return hit;
	}

	public int getHealth() {
		return health;
	}

	public void setCanJump(boolean canJump) {
		this.canJump = canJump;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void setJumping(final boolean jumping) {
		this.jumping = jumping;
	}

	public void setAttacking(final boolean attacking) {
		this.attacking = attacking;
	}

}
