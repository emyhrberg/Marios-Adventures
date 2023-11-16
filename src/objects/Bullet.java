package objects;

import constants.ObjectConstants.ObjectType;
import helpers.Sound;
import main.Level;
import main.Player;

import static main.Level.solidTiles;
import static ui.Menu.*;

public class Bullet extends GameObject {

    // Size
    public static final int BULLET_W = 50;
    public static final int BULLET_H = 38;
    private static final int HB_W = 36;
    private static final int HB_H = 20;
    public static final float BULLET_Y_OFF = 12;

    // Properties
    private static final float BULLET_SPEED = 0.7f;
    private static final float BULLET_DEATH_SPEED = 1.1f;

    public Bullet(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y + BULLET_Y_OFF * SCALE, HB_W, HB_H);
    }

    public void update(Player player, Level level, Bullet b) {
        if (b.isActive()) {
            updateAnimationTick();
            updatePlayerBulletCollision(level, player, b);
            updateBulletPos();
        }
    }

    private void updateBulletPos() {
        if (!isHit) {
            hitbox.x -= BULLET_SPEED * SCALE;
        } else {
            hitbox.y += BULLET_DEATH_SPEED * SCALE;
        }
    }

    private void updatePlayerBulletCollision(Level level, Player player, Bullet b) {
        // check the player isn't hit, the bullet isn't hit, and that player and bullet are colliding
        if (!player.isHit() && !isHit && hitbox.intersects(player.getHitbox())) {
            float playerBox = player.getHitbox().y + player.getHitbox().height;
            float bulletBox = hitbox.y + hitbox.height;
            float distBetweenBoxes = Math.abs(playerBox - bulletBox);
            float enemyHead = hitbox.height - 10 * SCALE;
            boolean isOnTopOfBullet = distBetweenBoxes > enemyHead;

            // BOUNCE ON BULLET
            if (isOnTopOfBullet && player.getAirSpeed() > 0) {
                player.jumpOnEnemy();
                setHit(true);
                Sound.play("/sounds/stomp.wav");
            } else {
                // TAKE DAMAGE FROM BULLET
                player.hitByBullet(b);
            }
        } else if (isBulletHittingLevel(level)) {
            setActive(false);
        }
    }

    private boolean isBulletHittingLevel(Level level) {
        int bulletX = (int) (hitbox.x / TILES_SIZE);
        int bulletY = (int) ((hitbox.y + hitbox.height) / TILES_SIZE);

        // Handle situation where bullet was not stopped by a solid tile
        // and must be removed when touching edge of level
        if (bulletX <= 0 || bulletY >= TILES_IN_HEIGHT) {
            setActive(false);
            return false;
        }

        return solidTiles.contains(level.getLevelData()[bulletY][bulletX]);
    }
}
