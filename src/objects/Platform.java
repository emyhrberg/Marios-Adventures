package objects;

import entities.Player;
import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Platform extends GameObject {

    // Platform hitbox
    public static final int PLATFORM_WIDTH_DEF = 32;
    public static final int PLATFORM_HEIGHT_DEF = 8;
    public static final int PLATFORM_WIDTH = (int) (PLATFORM_WIDTH_DEF * 2 * SCALE);
    public static final int PLATFORM_HEIGHT = (int) (PLATFORM_HEIGHT_DEF * 2 * SCALE);
    public static final int PLATFORM_Y_OFFSET = 5;
    private final Rectangle2D.Float bottom;
    private final Rectangle2D.Float top;

    // Platform moving
    private final long lastMoveTime = System.currentTimeMillis();
    private static final int MOVE_DURATION = 2000;
    private float direction;

    public Platform(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, PLATFORM_WIDTH_DEF * 2, PLATFORM_HEIGHT_DEF * 2);
        bottom = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y+5, hitbox.width, hitbox.height));
        top = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y, hitbox.width, 5));
    }

    public void update(Player player) {
        updateAnimationTick();
        updatePlatformPosition(player);
    }

    private void updatePlatformPosition(Player player) {
        // Odd seconds -> move right
        // Even seconds -> move left
        direction = -0.7f;
        if ((System.currentTimeMillis() - lastMoveTime) / MOVE_DURATION % 2 == 0) {
            direction = 0.7f;
        }

        // move platform position
        hitbox.x += direction;
        bottom.x += direction;
        top.x += direction;

        // move player with the platform's direction
        if (player.isOnPlatform()) {
            player.getHitbox().x += direction;
        }
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

    public float getDirection() {
        return direction;
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
