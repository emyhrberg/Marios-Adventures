package main;

import constants.Direction;
import objects.Platform;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static constants.Direction.*;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static main.Level.*;

/**
 * Represents any entity in the game, including the player and enemy
 * This abstract class defines common properties shared by all Entities.
 * Most of the collision handling is here in protected methods
 */
public class Entity {

	// ====== Falling on spawn =======
	public static final float GRAVITY = 0.05f * Game.SCALE;
	protected boolean inAir;
	protected float airSpeed;

	// ====== Position =======
	protected float x, y, width, height;
	protected float xDirection, xSpeed;
	protected Direction direction = STILL;

	// ====== Health =======
	protected int maxHealth, health;

	// ====== Attacking =======
	protected boolean attacking, attackChecked, hit, canAttack;

	// ====== Pushback =======
	protected Direction pushXDir;

	// ====== Hitboxes ======
	protected Rectangle2D.Float hitbox, attackBox;

	// ====== Constructor ======
	protected Entity(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	// ====== Tile collision ======

	protected boolean canMoveToPosition(float x, float y, float width, float height, Level level) {
		// Get x, y, width and height position for player and see if he can move to the next position or not
		boolean isTopLeftSolid = isSolid(x, y, level);
		boolean isTopRightSolid = isSolid(x + width, y, level);
		boolean isBottomLeftSolid = isSolid(x, y + height, level);
		boolean isBottomRightSolid = isSolid(x + width, y + height, level);

		// The entity can only move to a position if neither corner is touching something solid
		if (!isTopLeftSolid && !isTopRightSolid && !isBottomLeftSolid && !isBottomRightSolid) {
			return true;
		}

		return false;
	}

	protected boolean moveToPosition(float x, float y, float width, float height, Level level) {
		// Handle slopes first
		if (isDownSlope(level))
			return moveDownSlope();
		if (isUpSlope(level))
			return moveUpSlope(x+width);

		// Handle if entity can move to position
		if (canMoveToPosition(x, y, width, height, level) && health > 0) {
			// Update hitbox x and y position
			hitbox.x += x - hitbox.x;
			hitbox.y += y - hitbox.y;
			return true;
		}
		return false;
	}

	protected boolean isSolid(float x, float y, Level level) {
		// Set left and right edge of world to be solid when player walks into them
		int w = level.getLevelData()[0].length * TILES_SIZE - 1;
		if (x >= w || x <= 0) { return true; }

		int tileX = (int) (x / TILES_SIZE);
		int tileY = (int) (y / TILES_SIZE);

		// Fall through outside level
		if (isTileOutsideLevel(tileY)) { return false; }

		if (isTileSolid(tileX,tileY,level)) {
			return true;
		}

		return false;
	}

	protected boolean isTileSolid(int tileX, int tileY, Level level) {
		return !transparentTiles.contains(level.getLevelData()[tileY][tileX]);
	}

	protected boolean isTileOutsideLevel(int tileY) {
		return tileY < 0 || tileY >= Game.TILES_IN_HEIGHT;
	}

	// ====== Entity falling ======

	protected boolean isEntityInAir(Rectangle2D.Float hitbox, Level level) {
		// check platform
		// todo change to onPlatform?
		if (currentPlatform != null || inLava) {
			return false;
		}

		// check the bottom left and bottom right corner of the hitbox
		boolean isBottomLeftSolid = isSolid(hitbox.x, hitbox.y + hitbox.height + 1, level);
		boolean isBottomRightSolid = isSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, level);

		// slopes
		boolean isBottomLeftSlopedDown = isDownSlope(level);
		boolean isBottomRightSlopedUp = isUpSlope(level);

		// returns false if either hitbox for bottom left and bottom right corner is colliding with a solid tile
		return !isBottomLeftSolid && !isBottomRightSolid && !isBottomLeftSlopedDown && !isBottomRightSlopedUp;
	}

	// ====== Jumping ======

	protected boolean jumpAllowed = true;
	protected boolean jumping = false;
	protected static final float MAX_JUMP_HEIGHT = 3.5f * SCALE;
	protected float jumpHeight = MAX_JUMP_HEIGHT;

	protected void startFalling(Level level) {
		jumpHeight = MAX_JUMP_HEIGHT;
		if (moveToPosition(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, level)) {
			// Set ySpeed
			airSpeed += GRAVITY;
		} else {
			// Cannot move to position -> Stop falling, reset jump height
			if (airSpeed > 0) {
				airSpeed = 0;
				inAir = false;
			} else {
				airSpeed = GRAVITY * 8;
			}
		}
	}

	protected void updateDirection() {
		if (direction == LEFT)
			xDirection = -xSpeed;
		else if (direction == RIGHT)
			xDirection = xSpeed;
		else
			xDirection = 0;
	}

	// ====== Platforms ======

	private Platform currentPlatform;
	private boolean onPlatform = false;

	public void bindPlatform(Platform p) {
		if(currentPlatform != null || airSpeed <= 0) {
			return;
		}
		onPlatform = true;
		currentPlatform = p;
		this.setAirSpeed(0);
		this.setInAir(false);
		this.setJumping(false);
	}

	public void unbindPlatform() {
		currentPlatform = null;
		onPlatform = false;
	}

	public Rectangle2D.Float getHitboxTop() {
		return new Rectangle2D.Float(this.getHitbox().x,this.getHitbox().y,this.getHitbox().width,2);
	}

	public boolean isOnPlatform(Platform platform){
		return onPlatform && currentPlatform == platform;
	}

	// ====== Lava ======

	private boolean inLava = false;

	public void setInLava(boolean inLava) {
		this.inLava = inLava;
		this.setAirSpeed(0);
		this.setInAir(false);
		this.setJumping(false);
	}

	// ====== Slopes ======

	protected boolean moveDownSlope() {
		// Update x position
		hitbox.x += xDirection;

		// Do jump
		if (airSpeed < 0) {
			hitbox.y += airSpeed;
			return false;
		}

		// Get position within the tile
		float xInTile = hitbox.x % TILES_SIZE;

		// Push player over top of slope to avoid getting stuck
		if (xInTile < 2) {
			hitbox.y -= 1;
			return false;
		}

		// Move up the slope
		hitbox.y = (float) (Math.floor((hitbox.y - 2) / TILES_SIZE)) * TILES_SIZE + xInTile;

		// Move down the slope
		if (inAir) {
			hitbox.y = (float) (Math.floor((hitbox.y + hitbox.height) / TILES_SIZE) - 1) * TILES_SIZE + xInTile;
		}
		return false;
	}

	protected boolean moveUpSlope(float x) {
		// Update x position
		hitbox.x += xDirection;

		// Do jump
		if (airSpeed < 0) {
			hitbox.y += airSpeed;
			return false;
		}

		// Get position within the tile
		float xInTile = x % TILES_SIZE;

		// Push player over top of slope to avoid getting stuck
		if (xInTile > TILES_SIZE - 2) {
			hitbox.y -= 1;
			return false;
		}

		// Move up the slope
		hitbox.y = (float) (Math.floor((hitbox.y - 2) / TILES_SIZE)) * TILES_SIZE + TILES_SIZE - xInTile;

		// Move down the slope
		if (inAir) {
			hitbox.y = (float) (Math.floor((hitbox.y + hitbox.height) / TILES_SIZE) - 1) * TILES_SIZE + TILES_SIZE - xInTile;
		}
		return false;
	}

	protected boolean isDownSlope(Level level) {
		int tileX = (int) (hitbox.x / TILES_SIZE);
		int tileY = (int) (hitbox.y + hitbox.height) / TILES_SIZE;

		if (isTileOutsideLevel(tileY)) { return false; }

		int tileValue = level.getLevelData()[tileY][tileX];

		return tileValue == DOWN_SLOPE;
	}

	protected boolean isUpSlope(Level level) {
		int tileX = (int) (hitbox.x + hitbox.width) / TILES_SIZE;
		int tileY = (int) (hitbox.y + hitbox.height) / TILES_SIZE;

		if (isTileOutsideLevel(tileY)) { return false; }

		int tileValue = level.getLevelData()[tileY][tileX];
		return tileValue == UP_SLOPE;
	}

	// ====== Hitbox and Attackbox ======

	protected void initHitbox(float x, float y, float width, float height) {
		hitbox = new Rectangle2D.Float(x, y, width, height);
	}

	protected void initAttackBox(float width, float height) {
		attackBox = new Rectangle2D.Float(0,0, width, height);
	}

	public void drawHitbox(Graphics g, int levelOffset) {
		// draw hitbox
		g.setColor(new Color(255,0,0,80));
		g.fillRect((int) (hitbox.x - levelOffset), (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);

		// draw stroke
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(3)); // set stroke width
		g2d.setColor(Color.BLACK); // set stroke color
		g2d.drawRect((int) hitbox.x - levelOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height); // draw the rectangle outline
	}

	protected void drawAttackBox(Graphics g, int levelOffset) {
		g.setColor(new Color(255, 0, 255, 80));
		g.fillRect((int) attackBox.x - levelOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);

		// draw stroke
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(3)); // set stroke width
		g2d.setColor(Color.BLACK); // set stroke color
		g2d.drawRect((int) attackBox.x - levelOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height); // draw the rectangle outline
	}

	protected void updateAttackbox() {
		// Attackbox Y
		attackBox.y = hitbox.y;

		if (direction == RIGHT)
			attackBox.x = hitbox.x;
		if (direction == LEFT)
			attackBox.x = hitbox.x + hitbox.width - attackBox.width;
		if (hit)
			if (pushXDir == LEFT)
				attackBox.x = hitbox.x;
			else
				attackBox.x = hitbox.x + hitbox.width - attackBox.width;
	}

	// ====== Initializers =======

	protected void initSpeed(final float value) {
		this.xSpeed = value * Game.SCALE;
	}

	protected void initMaxHealth(final int value) {
		this.maxHealth = value;
		this.health = maxHealth;
	}

	// ====== Getters & Setters ======

	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setJumpHeight(float jumpHeight) {
		this.jumpHeight = jumpHeight;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public void setAirSpeed(float airSpeed) {
		this.airSpeed = airSpeed;
	}

	public float getAirSpeed() {
		return airSpeed;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}

}
