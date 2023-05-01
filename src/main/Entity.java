package main;

import constants.Direction;
import objects.Platform;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static constants.Direction.*;
import static main.Level.*;
import static ui.Menu.*;

/**
 * Represents any entity in the game, including the player and enemy
 * This abstract class defines common properties shared by all Entities.
 * Most of the collision handling is here in protected methods
 */
public class Entity {

	// ====== Falling on spawn =======
	public static final float GRAVITY = 0.05f;
	protected boolean inAir;
	protected float airSpeed;

	// ====== Position =======
	protected final float x;
	protected final float y;
	protected final float width;
	protected final float height;
	protected float xDirection;
	protected float xSpeed;
	protected Direction direction = STILL;

	// ====== Health =======
	protected int maxHealth, health;

	// ====== Attacking =======
	protected boolean attacking, attackChecked, hit;

	// ====== Pushback =======
	protected Direction pushXDir;

	// ====== Hitboxes ======
	protected Rectangle2D.Float hitbox, attackbox, rightbox, leftbox;

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
		return !isTopLeftSolid && !isTopRightSolid && !isBottomLeftSolid && !isBottomRightSolid;
	}

	protected boolean moveToPosition(float x, float y, float width, float height, Level level) {
		// Handle platforms
		handlePlatforms();

		// Handle slopes
		if (isDownSlope(level)) {
			moveDownSlope();
			return false;
		} if (isUpSlope(level)) {
			moveUpSlope(x + width);
			return false;
		}

		// Handle if entity can move to position
		if (canMoveToPosition(x, y, width, height, level) && health > 0) {

			// smooth movement
			float distanceX = x - hitbox.x;

			if (Math.abs(distanceX) < Math.abs(xDirection)) {
				// If the remaining distance is less than the current xDirection, snap to the target position
				hitbox.x = x;
			} else {
				// Gradually adjust the x position
				hitbox.x += xDirection;
			}

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

		return isTileSolid(tileX, tileY, level);
	}

	protected boolean isTileSolid(int tileX, int tileY, Level level) {
		return solidTiles.contains(level.getLevelData()[tileY][tileX]);
	}

	protected boolean isTileOutsideLevel(int tileY) {
		return tileY < 0 || tileY >= TILES_IN_HEIGHT;
	}

	// ====== Entity falling ======

	protected boolean isEntityInAir(Rectangle2D.Float hitbox, Level level) {
		if (currentPlatform != null || inLava)
			return false;

		// check the bottom left and bottom right corner of the hitbox
		boolean isBottomLeftSolid = isSolid(hitbox.x, hitbox.y + hitbox.height + 1, level);
		boolean isBottomRightSolid = isSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, level);

		// slopes
		boolean isBottomLeftSlopedDown = isDownSlope(level);
		boolean isBottomRightSlopedUp = isUpSlope(level);

		// returns false if either hitbox for bottom left and bottom right corner is colliding with a solid tile
		return !isBottomLeftSolid && !isBottomRightSolid && !isBottomLeftSlopedDown && !isBottomRightSlopedUp;
	}

	protected void startFalling(Level level) {
		if (moveToPosition(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, level)) {
			// Set ySpeed
			airSpeed += GRAVITY * SCALE;
		} else {
			// Cannot move to position -> Stop falling, reset jump height
			if (airSpeed > 0) {
				airSpeed = 0;
				inAir = false;
			} else {
				airSpeed = GRAVITY * SCALE;
			}
		}
	}

	// ====== Platforms ======

	private Platform currentPlatform;
	private float xOnPlatform;
	private boolean onPlatform;

	private void handlePlatforms() {
		if (currentPlatform != null) {
			xOnPlatform += xDirection;
			hitbox.x = xOnPlatform+currentPlatform.getTop().x;
			if (hitbox.x > currentPlatform.getTop().x+currentPlatform.getTop().width || hitbox.x+hitbox.width < currentPlatform.getTop().x) {
				unbindPlatform();
			}
		}
	}

	public void bindPlatform(Platform p) {
		if(currentPlatform != null || airSpeed <= 0) {
			return;
		}
		onPlatform = true;
		currentPlatform = p;
		xOnPlatform = hitbox.x- currentPlatform.getTop().x;
		hitbox.y = currentPlatform.getHitbox().y - hitbox.height;

		// set player to not in air
		setInAir(false);
		resetAirSpeed();
	}

	public void unbindPlatform() {
		currentPlatform = null;
		onPlatform = false;

	}

	public boolean isOnPlatform() {
		return onPlatform;
	}

	// ====== Lava ======

	private boolean inLava = false;

	public boolean isInLava() {
		return inLava;
	}

	public void setInLava(boolean inLava) {
		this.inLava = inLava;
	}

	// ====== Slopes ======

	private void moveDownSlope() {
		// Update x position
		hitbox.x += xDirection;

		// Do jump
		if (airSpeed < 0) {
			hitbox.y += airSpeed;
			return;
		}

		// Get position within the tile
		float xInTile = hitbox.x % TILES_SIZE;

		// Push player over top of slope to avoid getting stuck
		if (xInTile < 2) {
			hitbox.y -= 1;
			return;
		}

		// Move up the slope
		hitbox.y = (float) (Math.floor((hitbox.y - 2) / TILES_SIZE)) * TILES_SIZE + xInTile;

		// Move down the slope
		if (inAir) {
			hitbox.y = (float) (Math.floor((hitbox.y + hitbox.height) / TILES_SIZE) - 1) * TILES_SIZE + xInTile;
		}
	}

	private void moveUpSlope(float x) {
		// Update x position
		hitbox.x += xDirection;

		// Do jump
		if (airSpeed < 0) {
			hitbox.y += airSpeed;
			return;
		}

		// Get position within the tile
		float xInTile = x % TILES_SIZE;

		// Push player over top of slope to avoid getting stuck
		if (xInTile > TILES_SIZE - 2) {
			hitbox.y -= 1;
			return;
		}

		// Move up the slope
		hitbox.y = (float) (Math.floor((hitbox.y - 2) / TILES_SIZE)) * TILES_SIZE + TILES_SIZE - xInTile;

		// Move down the slope
		if (inAir) {
			hitbox.y = (float) (Math.floor((hitbox.y + hitbox.height) / TILES_SIZE) - 1) * TILES_SIZE + TILES_SIZE - xInTile;
		}
	}

	private boolean isDownSlope(Level level) {
		int tileX = (int) (hitbox.x / TILES_SIZE);
		int tileY = (int) (hitbox.y + hitbox.height) / TILES_SIZE;

		if (isTileOutsideLevel(tileY)) { return false; }

		int tileValue = level.getLevelData()[tileY][tileX];

		return tileValue == DOWN_SLOPE;
	}

	private boolean isUpSlope(Level level) {
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

	protected void initAttackbox(float x, float y, float width, float height) {
		attackbox = new Rectangle2D.Float(x, y, width, height);
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

	public void drawBox(Rectangle2D.Float box, Color color, Graphics g, int levelOffset) {
		// draw hitbox
		g.setColor(color);
		g.fillRect((int) (box.x - levelOffset), (int) box.y, (int) box.width, (int) box.height);

		// draw stroke
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(3)); // set stroke width
		g2d.setColor(Color.BLACK); // set stroke color
		g2d.drawRect((int) box.x - levelOffset, (int) box.y, (int) box.width, (int) box.height); // draw the rectangle outline
	}

	protected void drawAttackBox(Graphics g, int levelOffset) {
		g.setColor(new Color(255, 0, 255, 80));
		g.fillRect((int) attackbox.x - levelOffset, (int) attackbox.y, (int) attackbox.width, (int) attackbox.height);

		// draw stroke
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setStroke(new BasicStroke(3)); // set stroke width
		g2d.setColor(Color.BLACK); // set stroke color
		g2d.drawRect((int) attackbox.x - levelOffset, (int) attackbox.y, (int) attackbox.width, (int) attackbox.height); // draw the rectangle outline
	}

	protected void updateAttackbox() {
		// Attackbox Y
		attackbox.y = hitbox.y;

		if (direction == RIGHT)
			attackbox.x = hitbox.x;
		if (direction == LEFT)
			attackbox.x = hitbox.x + hitbox.width - attackbox.width;
		if (hit)
			if (pushXDir == LEFT)
				attackbox.x = hitbox.x;
			else
				attackbox.x = hitbox.x + hitbox.width - attackbox.width;
	}

	// ====== Initializers =======

	protected void initSpeed(final float value) {
		this.xSpeed = value * SCALE;
	}

	protected void initMaxHealth(final int value) {
		this.maxHealth = value;
		this.health = maxHealth;
	}

	// ====== Getters & Setters ======

	public Rectangle2D.Float getLeftbox() {
		return leftbox;
	}

	public Rectangle2D.Float getRightbox() {
		return rightbox;
	}

	public boolean isHit() {
		return hit;
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setInAir(boolean inAir) {
		this.inAir = inAir;
	}

	public void resetAirSpeed() {
		this.airSpeed = 0;
	}

	public float getAirSpeed() {
		return airSpeed;
	}

	public Rectangle2D.Float getHitbox() {
		return hitbox;
	}



}
