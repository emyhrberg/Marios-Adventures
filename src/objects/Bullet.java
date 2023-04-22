package objects;

import constants.ObjectConstants.ObjectType;

import static main.Game.SCALE;

public class Bullet extends GameObject {

    // Size
    public static final int BULLET_W_DEF = 37;
    public static final int BULLET_H_DEF = 22;
    public static final int BULLET_W = (int) (BULLET_W_DEF * SCALE);
    public static final int BULLET_H = (int) (BULLET_H_DEF * SCALE);
    public static final int BULLET_X_OFFSET = (int) (20 * SCALE);
    public static final int BULLET_Y_OFFSET = (int) (-5 * SCALE);

    public Bullet(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x,y,BULLET_W_DEF,BULLET_H_DEF);
    }

    public void updateBulletPos() {
        hitbox.x -= 1;
    }
}
