package main;

import helpers.ImageLoader;
import helpers.SoundLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

import static constants.Direction.*;
import static constants.GameState.GAME_OVER;
import static constants.PlayerConstants.PlayerAction;
import static constants.PlayerConstants.PlayerAction.*;
import static constants.PlayerConstants.getSpriteAmount;
import static main.Game.SCALE;

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
    private PlayerAction playerAction 					= IDLE;

    // ====== Player Size ======
    private static final BufferedImage PLAYER_SPRITES 	= ImageLoader.loadImage("/images/sprites_player.png");
    public static final int PLAYER_WIDTH 				= 50;
    public static final int PLAYER_HEIGHT 				= 50;
    private static final int IMAGE_X_OFFSET 			= (int) (14 * SCALE);
    private static final int IMAGE_Y_OFFSET 			= (int) (18 * SCALE);
    private static final int HITBOX_WIDTH 				= (int) (30 * SCALE);
    private static final int HITBOX_HEIGHT 				= (int) (39 * SCALE);
    private static final int ATTACKBOX_WIDTH 			= HITBOX_WIDTH * 3;
    private static final int ATTACKBOX_HEIGHT 			= HITBOX_HEIGHT;
	private int imageFlipX, imageFlipWidth = 1;

    // ====== Player Settings ======
    private static final float SPEED					= 0.7f * SCALE;
    private static final int MAX_HEALTH 				= 2000;

    // ====== Game variables =======
    private final Game game;
    private Level level;

    // ====== Constructor =======
    public Player(float x, float y, float width, float height, Game game) {
		super(x, y, width, height);
		this.game = game;

		setDirection(STILL);
		initSpeed(SPEED);
		initMaxHealth(MAX_HEALTH);
		initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
		initAttackBox(ATTACKBOX_WIDTH, ATTACKBOX_HEIGHT);
		initAnimations();
    }

    public void update() {
		updatePos();
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

		if (jumping) {
			if (canJump) {
				jump();
			}
		}
	}

	public void jump() {
		if (inAir || !canJump)
			return;
		inAir = true;
		airSpeed = -jumpHeight;
		canJump = false;
		jumpHeight = MAX_JUMP_HEIGHT;
		unbindPlatform();
	}

    private void updateAttacking() {
		if (attacking) {
			// Do not attack on the first animation index
			if (animationIndex == 0)
				attackChecked = false;

			// Only deal damage on the last animation index
			final int lastAttackAniIndex = 3;
			if (animationIndex == lastAttackAniIndex && !attackChecked) {
				game.getPlaying().getEnemyManager().dealDamageToEnemy(this);
				attackChecked = true;
			}
		}
    }

    // ====== Modify player variables ======

	public void jumpOnEnemy() {
		jumpHeight = (float) (0.7 * MAX_JUMP_HEIGHT);
		airSpeed = -jumpHeight;
		inAir = true;
		SoundLoader.playAudio("jump.wav", 0.8);
	}

    public void reducePlayerHealth(int value, Enemy enemy) {
		health -= value;
		hit = true;

		SoundLoader.playAudio("player_taking_damage.wav");

		// Update push back direction
		if (enemy.getHitbox().x < hitbox.x)
			pushBackDirection = RIGHT;
		else
			pushBackDirection = LEFT;
    }

    public void resetPlayer() {
		inAir = false;
		attacking = false;
		hit = false;
		health = maxHealth;
		direction = STILL;
    }

    private void pushBack() {
		// Update xDirection based on pushBackDirection
		if (pushBackDirection == LEFT)
			xDirection = -xSpeed;
		if (pushBackDirection == RIGHT)
			xDirection = xSpeed;

		// Move player X, direction with double direction
		moveToPosition((float) (hitbox.x + xDirection * 0.8), hitbox.y, hitbox.width, hitbox.height, level);

		// Move player Y, small jump
		pushbackY();
    }

	private void pushbackY() {
		jumpHeight = (float) (0.7 * MAX_JUMP_HEIGHT);
		jump();
		canJump = true;
	}

    // ====== Draw and Animations ======

    public void draw(Graphics g, int levelOffset) {
		// Update player X and width based on the direction to flip the image
		updateImageFlip();

		// Get x, y, width and height in order to draw the animations
		final float x = hitbox.x - levelOffset - IMAGE_X_OFFSET + imageFlipX;
		final float y = hitbox.y - IMAGE_Y_OFFSET;
		final float w = width * imageFlipWidth;
		final float h = height;

		// Get the proper image representing the right action
		final int action = playerAction.ordinal();
		final BufferedImage img = playerImages[action][animationIndex];

		// Draw the image according to the player action and loop through the animation index to show all the images
		g.drawImage(img, (int) x, (int) y, (int) w, (int) h, null);

		updateAttackBox();

		// Debug hitbox
//		drawHitbox(g, levelOffset);
//		drawAttackBox(g, levelOffset);
    }

    private void updateImageFlip() {
		// When facing right, draw default with X at 0 and width at 1
		if (direction == RIGHT) {
			imageFlipX = 0;
			imageFlipWidth = 1;
		}
		// When facing left, draw from left to right by drawing an extra width and flipping the image width with -1
		if (direction == LEFT) {
			imageFlipX = (int) ((int) (width) / 1.15);
			imageFlipWidth = -1;
		}
    }

    private void initAnimations() {
		// Loop through all images and populate the animations 2d array with each subimage
		for (int row = 0; row < ROWS; row++)
			for (int rowIndex = 0; rowIndex < IMAGES_IN_ROW; rowIndex++) {
				int x = rowIndex * PLAYER_WIDTH;
				int y = row * PLAYER_HEIGHT;
				playerImages[row][rowIndex] = PLAYER_SPRITES.getSubimage(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
			}
    }

    public void updateAnimationTick() {
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
			health = 0;
			playerDeathAndGameOver();
		} else if (hit) {
			playerAction = HIT;
			pushBack();
		} else if (attacking) {
			playerAction = ATTACKING;
		} else if (inAir && airSpeed < 0) {
			// todo add better jumping anim
			playerAction = JUMPING;
		} else if (inAir && airSpeed > 0) {
			// todo add falling
			playerAction = JUMPING;
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

	public void playerDeathAndGameOver() {
		if (playerAction != DYING) {
			playerAction = DYING;
			animationIndex = 0;
			animationTick = 0;
		}

		if (animationIndex == getSpriteAmount(DYING) - 1) {
			// here we reached final death animation shown, now set game over!
			game.setGameState(GAME_OVER);
		}
	}

    // ====== Getters & Setters ======

	public boolean isHit() {
		return hit;
	}

	public int getMaxHealth() {
		return maxHealth;
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
