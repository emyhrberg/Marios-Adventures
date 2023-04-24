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
    private Direction platDir = LEFT;
    private static final float MAX_SPEED = 0.5f;
    private float platformSpeed;

    public Platform(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, PLATFORM_WIDTH_HITBOX, PLATFORM_HEIGHT_DEF);
        bottom = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y+5, hitbox.width, hitbox.height));
        top = (Rectangle2D.Float) hitbox.createIntersection(new Rectangle2D.Float(hitbox.x, hitbox.y, hitbox.width, 5));
    }

    public void update(Player player, Level level, Platform p) {
        updateAnimationTick();
        updatePlatformPosition(player, level);
        updatePlatformBinding(player, p);
    }

    private void updatePlatformBinding(Player player, Platform p) {
        if (p.getTop().intersects(player.getHitbox())) {
            player.bindPlatform();
        } else {
            // todo if not on a platform, unbind
//            player.unbindPlatform();
        }
    }

    private void updatePlatformPosition(Player player, Level level) {
        if (platDir == RIGHT)
            platformSpeed = MAX_SPEED;
        if (platDir == LEFT)
            platformSpeed = -MAX_SPEED;

        if (hitSolidTileLeft(level))
            platDir = RIGHT;
        if (hitSolidTileRight(level))
            platDir = LEFT;


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

    public Rectangle2D.Float getTop() {
        return top;
    }

    // draw

    protected void drawSomeBox(Rectangle2D.Float box, Color color, Graphics g, int levelOffset) {
        // draw hitbox
        g.setColor(color);
        g.fillRect((int) (box.x - levelOffset), (int) box.y, (int) box.width, (int) box.height);

        // draw stroke
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setStroke(new BasicStroke(2)); // set stroke width
        g2d.setColor(Color.BLACK); // set stroke color
        g2d.drawRect((int) box.x - levelOffset, (int) box.y, (int) box.width, (int) box.height); // draw the rectangle outline
    }

}
