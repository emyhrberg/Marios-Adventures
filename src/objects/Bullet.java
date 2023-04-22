package objects;

import constants.ObjectConstants.ObjectType;

import static main.Game.SCALE;

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
}
