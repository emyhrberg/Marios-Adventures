package objects;

import constants.ObjectConstants.ObjectType;
import main.Level;

import java.util.List;

import static main.Game.SCALE;
import static main.Game.TILES_SIZE;
import static main.Level.transparentTiles;

public class Bullet extends GameObject {

    // Size
    public static final int BULLET_W_DEF = 50;
    public static final int BULLET_H_DEF = 38;
    public static final int BULLET_W = (int) (BULLET_W_DEF * 0.8 * SCALE);
    public static final int BULLET_H = (int) (BULLET_H_DEF * 0.8 * SCALE);
    public static final int BULLET_X_OFFSET = (int) (5 * SCALE);
    public static final int BULLET_Y_OFFSET = (int) (-3 * SCALE);
    private static final int BULLET_HB_W = 30;
    private static final int BULLET_HB_H = 18;

    // Properties
    private static final int BULLET_SPEED = (int) (0.7 * SCALE);

    public Bullet(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x,y,BULLET_HB_W,BULLET_HB_H);
    }

    public void update() {
        updateAnimationTick();
        updateBulletPos();
    }

    private void updateBulletPos() {
        hitbox.x -= BULLET_SPEED;
    }

    public boolean isBulletHittingLevel(Bullet b, Level level, List<Bullet> bullets) {
        int bulletX = (int) (b.hitbox.x / TILES_SIZE);
        int bulletY = (int) (b.hitbox.y / TILES_SIZE);

        // Handle bullet outside the leftmost part of level
        if (bulletX <= 0) {
            return false;
        }

        if (!transparentTiles.contains(level.getLevelData()[bulletY][bulletX])) {
            return true;
        }
        return false;
    }
}
