package main;

import helpers.ImageLoader;
import helpers.Sound;
import objects.Bullet;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static constants.Direction.*;
import static constants.GameState.GAME_OVER;
import static constants.PlayerConstants.PlayerAction;
import static constants.PlayerConstants.PlayerAction.*;
import static constants.PlayerConstants.getSpriteAmount;
import static main.Game.UPS;
import static ui.Menu.SCALE;

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
	private static final int PLAYER_X_OFF 				= 14;
	private static final int PLAYER_Y_OFF 				= 18;
	private static final int HITBOX_WIDTH 				= 30;
	private static final int HITBOX_HEIGHT 				= 39;
	private static final int ATTACKBOX_WIDTH 			= HITBOX_WIDTH * 3;
	private static final int ATTACKBOX_HEIGHT 			= HITBOX_HEIGHT;
	private int imageFlipX, imageFlipWidth = 1;

	// ====== Player Settings ======
	private PlayerAction playerAction 					= IDLE;
	private static final float SPEED					= 1.1f;
	private static final int START_HEALTH 				= 5;
	private static final float ACCELERATION 			= 0.05f;

	// ====== Jumping ======
	private static final float MAX_JUMP_HEIGHT 			= 3.2f;
	private float jumpHeight = MAX_JUMP_HEIGHT;
	private boolean canJump = true;
	private boolean jumping = false;
	private long lastJumpTime;
	private static final int JUMP_MAX_BOOST_TIME = UPS;
	private boolean holdingSpace;

	// ====== Collision cooldown ======
	private long lastCheck;
	private boolean canCollide = true;

	// ====== Game variables =======
	private final Game game;
	private Level level;

	// ====== Constructor =======

	public Player(float width, float height, Game game) {
		super(0,0, width, height);
		this.game = game;

		setDirection(STILL);
		initSpeed(SPEED * SCALE);
		initMaxHealth(START_HEALTH);
		initHitbox(x, y, HITBOX_WIDTH * SCALE, HITBOX_HEIGHT * SCALE);
		initAttackbox(x, y, ATTACKBOX_WIDTH * SCALE, ATTACKBOX_HEIGHT * SCALE);
		initImages();

		// temp
		leftbox = new Rectangle2D.Float(x - 5, y, 10, height);
		rightbox = new Rectangle2D.Float(x + width, y, 5, height);
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
		updateSideBox();
		updatePos();
		updateAttackbox();
		updateAttacking();
		updateAnimationTick();
		updateAnimation();
	}

	private void updateSideBox() {
		// rightbox
		rightbox.x = hitbox.x + hitbox.width - 5;
		rightbox.y = hitbox.y;
		rightbox.width = 10;
		rightbox.height = hitbox.height;

		// leftbox
		leftbox.x = hitbox.x - 5;
		leftbox.y = hitbox.y;
		leftbox.width = 10;
		leftbox.height = hitbox.height;
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
		if (holdingSpace && airSpeed < 0 && maxJumpBoost && !hit) {
			airSpeed -= GRAVITY * 1.4 * SCALE;
		}

		if (canJump && jumping) {
			jump();
		}
	}


	private void updateDirection() {
		float targetXDirection = 0.0f;

		if (direction == LEFT) {
			targetXDirection = -xSpeed;
		} else if (direction == RIGHT) {
			targetXDirection = xSpeed;
		}

		if (xDirection < targetXDirection) {
			xDirection += ACCELERATION * SCALE;
			if (xDirection > targetXDirection) {
				xDirection = targetXDirection;
			}
		} else if (xDirection > targetXDirection) {
			xDirection -= ACCELERATION * SCALE;
			if (xDirection < targetXDirection) {
				xDirection = targetXDirection;
			}
		}
	}

	public void jump() {
		if (inAir || !canJump)
			return;

		unbindPlatform();

		// start jump
		lastJumpTime = System.currentTimeMillis();
		jumpHeight = MAX_JUMP_HEIGHT * SCALE;
		inAir = true;
		airSpeed = -jumpHeight;

		// reset jump
		jumpHeight = MAX_JUMP_HEIGHT * SCALE;
		jumping = false;
		canJump = false;

		Sound.play("/sounds/jump.wav");
	}

	public void jumpOnEnemy() {
		// set jump
		jumpHeight = MAX_JUMP_HEIGHT * SCALE;
		inAir = true;
		airSpeed = -jumpHeight;

		// reset jump
		jumpHeight = MAX_JUMP_HEIGHT;
		jumping = false;

		// disallow damage or bounce for 200 ms
		lastCheck = System.currentTimeMillis();
		canCollide = false;
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
		if (enemy.getHitbox().x < hitbox.x)
			pushXDir = RIGHT; // set player push dir
		else
			pushXDir = LEFT;

		ouchPlayer();
	}

	public void hitByBullet(Bullet b) {
		if (b.getHitbox().x < hitbox.x)
			pushXDir = RIGHT; // set player push dir
		else
			pushXDir = LEFT;

		ouchPlayer();
	}

	private void ouchPlayer() {
		health -= 1;
		hit = true;
		jumpOnEnemy();
		Sound.play("/sounds/ouchplayer.wav");
	}

	public void resetPlayer() {
		airSpeed = 0;
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
		final float x = hitbox.x - levelOffset - PLAYER_X_OFF * SCALE + imageFlipX;
		final float y = hitbox.y - PLAYER_Y_OFF * SCALE;
		final float w = width * imageFlipWidth;

		// Get the proper image representing the right action
		final int action = playerAction.ordinal();
		final BufferedImage img = playerImages[action][animationIndex];

		// Draw the image according to the player action and loop through the animation index to show all the images
		g.drawImage(img, (int) x, (int) y, (int) w, (int) height, null);

//		drawHitbox(g, levelOffset);
//		drawBox(leftbox, new Color(255,0,255,100), g, levelOffset);
//		drawBox(rightbox, new Color(40,56,210,100), g, levelOffset);
//		drawAttackBox(g, levelOffset);

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

		// Move player X
		moveToPosition(hitbox.x, hitbox.y, hitbox.width, hitbox.height, level);
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

	public void setPlayerAction(PlayerAction playerAction) {
		this.playerAction = playerAction;
	}

	public int disallowCollision() {
		return 200;
	}

	public void setCanCollide(boolean canCollide) {
		this.canCollide = canCollide;
	}

	public boolean canCollide() {
		return canCollide;
	}

	public long getLastCheck() {
		return lastCheck;
	}

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
