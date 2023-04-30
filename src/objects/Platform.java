package objects;

import constants.Direction;
import main.Game;
import main.Level;
import main.Player;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static constants.Direction.LEFT;
import static constants.Direction.RIGHT;
import static constants.ObjectConstants.ObjectType;
import static main.Game.TILES_SIZE;
import static main.Level.solidTiles;

public class Platform extends GameObject {

    // Platform hitbox
    public static final int PLATFORM_WIDTH_HITBOX = 20 * 2 + 4;
    public static final int PLATFORM_W = 32 * 2;
    public static final int PLATFORM_H = 8 * 2;
    public static final int PLATFORM_Y_OFF = 5;
    private final Rectangle2D.Float bottom;
    private final Rectangle2D.Float top;

    // Platform moving
    private Direction platDir = LEFT;
    private static final float MAX_SPEED = 0.9f * Game.SCALE;
    private float platSpeed;

    public Platform(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y + PLATFORM_Y_OFF, PLATFORM_WIDTH_HITBOX, PLATFORM_H);
        bottom = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y+5, hitbox.width, hitbox.height));
        top = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x-6, hitbox.y, hitbox.width, 5));
    }

    public void update(Player player, Level level, Platform p) {
        updateAnimationTick();
        updatePlatformPos(player, level);
        updatePlatformBinding(player, p);
    }

    private void updatePlatformBinding(Player player, Platform p) {
        // Player collision with bottom or sides of platform, set player in air
        if (p.getBottom().intersects(player.getHitbox())) {
            player.setInAir(true);
            if (!p.getBottomLine().intersects(player.getHitboxTop())) {
                player.getHitbox().x = p.getXOfClosestHitbox(player);
            }

        // Player on top of the platform
        } else if (p.getTop().intersects(player.getHitbox())) {
            player.bindPlatform(p);
        }
    }

    private void updatePlatformPos(Player player, Level level) {
        if (platDir == RIGHT)
            platSpeed = MAX_SPEED;
        if (platDir == LEFT)
            platSpeed = -MAX_SPEED;

        if (hitSolidTileLeft(level))
            platDir = RIGHT;
        if (hitSolidTileRight(level))
            platDir = LEFT;

        // Move platform position
        hitbox.x += platSpeed;
        bottom.x += platSpeed;
        top.x += platSpeed;

        // move player with the platform's direction
        if (player.isOnPlatform(this)) {
            player.getHitbox().x += platSpeed;
        }
    }

    public float getXOfClosestHitbox(Player player) {
        float left = Math.abs(player.getHitbox().x-hitbox.x);
        float right = Math.abs(player.getHitbox().x+ player.getHitbox().width - (hitbox.x+hitbox.width));
        return left < right ? hitbox.x-player.getHitbox().width: (hitbox.x+ hitbox.width);
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

    private boolean hitSolidTileLeft(Level level) {
        int tileX = (int) (hitbox.x / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        int distanceToTile = 0;

        if (tileX <= 0)
            return false;

        return solidTiles.contains(level.getLevelData()[tileY][tileX - distanceToTile]);
    }

    private boolean hitSolidTileRight(Level level) {
        int tileX = (int) (hitbox.x / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        if (tileX <= 0)
            return false;

        int distanceToTile = 0;

        return solidTiles.contains(level.getLevelData()[tileY][tileX + 1 + distanceToTile]);
    }


}
