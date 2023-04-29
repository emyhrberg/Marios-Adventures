package objects;

import constants.ObjectConstants.ObjectType;
import helpers.SoundLoader;
import main.Game;
import main.Level;
import main.Player;

import static main.Game.TILES_IN_HEIGHT;
import static main.Game.TILES_SIZE;
import static main.Level.solidTiles;

public class Bullet extends GameObject {

    // Size
    public static final int BULLET_W_DEF = 50;
    public static final int BULLET_H_DEF = 38;
    public static final int BULLET_W = (int) (BULLET_W_DEF * Game.SCALE);
    public static final int BULLET_H = (int) (BULLET_H_DEF * Game.SCALE);
    private static final int HB_W = 36;
    private static final int HB_H = 20;
    private static final float Y_OFF = 12 * Game.SCALE;
    public static final float Y_DRAW_OFF = 7 * Game.SCALE;

    // Properties
    private static final float BULLET_SPEED = 0.7f * Game.SCALE;
    private static final float BULLET_DEATH_SPEED = 1.1f * Game.SCALE;

    // cooldown
    private boolean canCollide;
    private long lastCheck;
    private static final int COLLISION_DELAY = 1000;


    public Bullet(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y + Y_OFF, HB_W, HB_H);
    }

    public void update(Player player, Level level, Bullet b) {
        if (b.isActive()) {
            updateAnimationTick();
            updateBulletCollision(level, player, b);
            updateBulletPos();
        }
    }

    private void updateBulletPos() {
        if (!isHit) {
            hitbox.x -= BULLET_SPEED;
        } else {
            hitbox.y += BULLET_DEATH_SPEED;
        }
    }

    private void updateBulletCollision(Level level, Player player, Bullet b) {
        canCollide = System.currentTimeMillis() - lastCheck >= COLLISION_DELAY;

        if (hitbox.intersects(player.getHitbox())) {
            float playerBox = player.getHitbox().y + player.getHitbox().height;
            float bulletBox = hitbox.y + hitbox.height;
            float distBetweenBoxes = Math.abs(playerBox - bulletBox);
            float enemyHead = hitbox.height - 10 * Game.SCALE;
            boolean isOnTopOfBullet = distBetweenBoxes > enemyHead;

            if (canCollide && !isHit) {
                // BOUNCE ON BULLET
                if (isOnTopOfBullet && player.getAirSpeed() > 0) {
                    player.jumpOnEnemy();
                    setHit(true);
                    lastCheck = System.currentTimeMillis();
                    SoundLoader.playSound("/sounds/stomp.wav", 0.8);
                } else {
                    // TAKE DAMAGE FROM BULLET
                    player.hitByBullet(b);
                    lastCheck = System.currentTimeMillis();
                }
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
