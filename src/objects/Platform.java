package objects;

import constants.Direction;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static constants.Direction.LEFT;
import static constants.Direction.RIGHT;
import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static main.Level.transparentTiles;

public class Platform extends GameObject {

    // Platform hitbox
    public static final int PLATFORM_WIDTH_HITBOX = 20 * 2;
    public static final int PLATFORM_WIDTH_DEF = 32 * 2;
    public static final int PLATFORM_HEIGHT_DEF = 8 * 2;
    public static final int PLATFORM_WIDTH = (int) (PLATFORM_WIDTH_DEF * SCALE);
    public static final int PLATFORM_HEIGHT = (int) (PLATFORM_HEIGHT_DEF * SCALE);
    public static final int PLATFORM_Y_OFFSET = 5;
    private final Rectangle2D.Float bottom;
    private final Rectangle2D.Float top;

    // Platform moving

    public Platform(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, PLATFORM_WIDTH_HITBOX, PLATFORM_HEIGHT_DEF);
        bottom = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y+5, hitbox.width, hitbox.height));
        top = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y, hitbox.width, 5));
    }

    public void update(Player player, Level level) {
        updateAnimationTick();
        updatePlatformPosition(player, level);
    }

    private Direction direction = RIGHT;
    private static final float SPEED = 1.5f;
    private float platformSpeed;

    private void updatePlatformPosition(Player player, Level level) {
        if (direction == RIGHT) {
            platformSpeed = SPEED;
        } else if (direction == LEFT) {
            platformSpeed = -SPEED;
        }

        if (hitSolidTileLeft(level)) {
            direction = RIGHT;
        } else if (hitSolidTileRight(level)) {
            direction = LEFT;
        }

        // Move platform position
        hitbox.x += platformSpeed;
        bottom.x += platformSpeed;
        top.x += platformSpeed;

        // move player with the platform's direction
        if (player.isOnPlatform()) {
            player.getHitbox().x += platformSpeed;
        }
    }

    private boolean hitSolidTileLeft(Level level) {
        int tileX = (int) (hitbox.x / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        int distanceToTile = 0;

        return !transparentTiles.contains(level.getLevelData()[tileY][tileX - distanceToTile]);
    }

    private boolean hitSolidTileRight(Level level) {
        int tileX = (int) (hitbox.x / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        int distanceToTile = 0;

        return !transparentTiles.contains(level.getLevelData()[tileY][tileX + 1 + distanceToTile]);
    }

    // getters

    public float getXOfClosestHitbox(Player player) {
        float distanceToLeft = Math.abs(hitbox.x - player.getHitbox().x + player.getHitbox().width);
        float distanceToRight = Math.abs(hitbox.x + hitbox.width - player.getHitbox().x);

        if (distanceToLeft < distanceToRight) {
            return hitbox.x - player.getHitbox().width;
        } else {
            return hitbox.x + hitbox.width;
        }
    }

    public Rectangle2D.Float getBottom() {
        return bottom;
    }

    public Rectangle2D.Float getTop() {
        return top;
    }

    public Rectangle2D.Float getBottomLine() {
        return new Rectangle.Float(bottom.x,bottom.y+bottom.height-1,bottom.width,3);
    }

}
