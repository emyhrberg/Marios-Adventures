package objects;

import constants.ObjectConstants.ObjectType;
import main.Level;
import main.Player;

import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static main.Level.transparentTiles;

public class Bullet extends GameObject {

    // Size
    public static final int BULLET_W_DEF = 50;
    public static final int BULLET_H_DEF = 38;
    public static final int BULLET_W = (int) (BULLET_W_DEF * 0.8 * SCALE);
    public static final int BULLET_H = (int) (BULLET_H_DEF * 0.8 * SCALE);
    private static final int HB_W = 30;
    private static final int HB_H = 18;
    private static final float Y_OFF = 8 * SCALE;
    public static final float Y_DRAW_OFF = 3 * SCALE;

    // Properties
    private static final int BULLET_SPEED = (int) (0.7 * SCALE);

    public Bullet(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y + Y_OFF, HB_W, HB_H);
    }

    public void update(Level level, Player player, Bullet b) {
        updateAnimationTick();
        updateBulletCollision(level, player, b);
        updateBulletPos();
    }

    private void updateBulletPos() {
        hitbox.x -= BULLET_SPEED;
    }

    private void updateBulletCollision(Level level, Player player, Bullet b) {
        if (b.hitbox.intersects(player.getHitbox())) {
            float playerBox = player.getHitbox().y + player.getHitbox().height;
            float bulletBox = hitbox.y + hitbox.height;
            float distBetweenBoxes = Math.abs(playerBox - bulletBox);
            float enemyHead = hitbox.height - 10 * SCALE;
            boolean isOnTopOfBullet = distBetweenBoxes > enemyHead;

            // either jump on enemy if on top of the bullet, or take damage
            if (isOnTopOfBullet && player.getAirSpeed() > 0) {
                player.jumpOnEnemy();
            } else {
                player.hitByBullet(b);
            }
            b.setActive(false);
        } else if (b.isBulletHittingLevel(b, level)) {
            b.setActive(false);
        }
    }

    private boolean isBulletHittingLevel(Bullet b, Level level) {
        int bulletX = (int) (b.hitbox.x / TILES_SIZE);
        int bulletY = (int) (b.hitbox.y / TILES_SIZE);

        // Handle situation where bullet was not stopped by a solid tile
        // and must be removed when touching edge of leftmost level
        if (bulletX <= 0) {
            b.setActive(false);
            return false;
        }

        if (!transparentTiles.contains(level.getLevelData()[bulletY][bulletX])) {
            return true;
        }
        return false;
    }
}
