package objects;

import constants.Direction;
import main.Level;
import main.Player;

import java.awt.geom.Rectangle2D;

import static constants.Direction.LEFT;
import static constants.Direction.RIGHT;
import static constants.ObjectConstants.ObjectType;
import static main.Level.solidTiles;
import static ui.Menu.SCALE;
import static ui.Menu.TILES_SIZE;

public class Platform extends GameObject {

    // Platform hitbox
    public static final int PLATFORM_W = 40 * 2;
    public static final int PLATFORM_H = 16 * 2;
    private final Rectangle2D.Float top;
    private final Rectangle2D.Float left;
    private static final float EXTRA_WIDTH = 5;

    // Platform moving
    private Direction platDir = LEFT;
    private static final float MAX_SPEED = 0.9f;
    private float platSpeed;

    public Platform(int x, int y, ObjectType objectType) {
        super(x, y, objectType);

        initHitbox(x - PLATFORM_W / 2f, y, PLATFORM_W, PLATFORM_H);
        top = new Rectangle2D.Float(hitbox.x - EXTRA_WIDTH / 2 * SCALE, hitbox.y, hitbox.width + EXTRA_WIDTH * SCALE, 1);
        left = new Rectangle2D.Float(hitbox.x - EXTRA_WIDTH / 2 * SCALE * SCALE, hitbox.y, hitbox.width + EXTRA_WIDTH * SCALE * SCALE, 1);
    }

    public void update(Player player, Level level, Platform p) {
        updateAnimationTick();
        updatePlatformPos(player, level);
        updatePlatformBinding(player, p);
    }

    private void updatePlatformBinding(Player player, Platform p) {
        if (p.getLeft().intersects(player.getRightbox())) {
            player.setInAir(true);
        } else if (p.getTop().intersects(player.getHitbox()))
            player.bindPlatform(p);
    }

    private void updatePlatformPos(Player player, Level level) {
        if (platDir == RIGHT)
            platSpeed = MAX_SPEED * SCALE;
        if (platDir == LEFT)
            platSpeed = -MAX_SPEED * SCALE;

        if (hitSolidTileLeft(level))
            platDir = RIGHT;
        if (hitSolidTileRight(level))
            platDir = LEFT;


        // Move platform position
        hitbox.x += platSpeed;
        top.x += platSpeed;

        // move player with the platform's direction
        if (player.isOnPlatform())
            player.getHitbox().x += platSpeed;
    }

    public Rectangle2D.Float getTop() {
        return top;
    }

    public Rectangle2D.Float getLeft() {
        return left;
    }

    private boolean hitSolidTileLeft(Level level) {
        int tileX = (int) (hitbox.x / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        int distanceToTile = 0;

        if (tileX < 0) return false;

        return solidTiles.contains(level.getLevelData()[tileY][tileX - distanceToTile]);
    }

    private boolean hitSolidTileRight(Level level) {
        int tileX = (int) (hitbox.x / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        int distanceToTile = 1;

        if (tileX < 0) return false;

        return solidTiles.contains(level.getLevelData()[tileY][tileX + 1 + distanceToTile]);
    }


}
