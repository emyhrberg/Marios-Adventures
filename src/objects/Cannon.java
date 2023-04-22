package objects;

import constants.ObjectConstants.ObjectType;

import static main.Game.SCALE;

public class Cannon extends GameObject {

    // Size
    public static final int CANNON_W_DEF = 50;
    public static final int CANNON_H_DEF = 80;
    public static final int CANNON_WIDTH = (int) (CANNON_W_DEF *1.2*SCALE);
    public static final int CANNON_HEIGHT = (int) (CANNON_H_DEF *1.2*SCALE);
    public static final int CANNON_X_OFFSET = (int) (16 * SCALE);

    // Properties
    private static final int CANNON_DELAY = 7000;

    public Cannon(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, CANNON_W_DEF, CANNON_H_DEF);
    }

    public void update() {
        shootAllowed = System.currentTimeMillis() >= lastCannonShot + CANNON_DELAY;

        if (shootAllowed) {
            updateCannonAnimation();
        } else if (animationIndex >= 4) {
            updateCannonAnimation();
            if (animationIndex >= 7) {
                animationIndex = 1;
            }
        }
    }

    private long lastCannonShot;
    private boolean shootAllowed;

    public void setLastCannonShot(long lastCannonShot) {
        this.lastCannonShot = lastCannonShot;
    }

    private void updateCannonAnimation() {
        animationTick++;

        if (animationTick >= ANIMATION_SPEED * 1.5) {
            animationTick = 0;
            animationIndex++;

            if (animationIndex >= 7) {
                animationIndex = 1;
            }
        }
    }

    // getters and stuff


    public boolean isShootAllowed() {
        return shootAllowed;
    }
}
