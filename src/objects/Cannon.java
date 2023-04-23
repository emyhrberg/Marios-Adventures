package objects;

import constants.ObjectConstants.ObjectType;

import static main.Game.SCALE;

public class Cannon extends GameObject {

    // Size
    public static final int CANNON_W_DEF = 50;
    public static final int CANNON_H_DEF = 80;
    public static final int CANNON_WIDTH = (int) (CANNON_W_DEF *1.3*SCALE);
    public static final int CANNON_HEIGHT = (int) (CANNON_H_DEF *1.3*SCALE);
    private static final float X_OFF = 22f * SCALE;
    private static final float Y_OFF = 6f * SCALE;

    // Properties
    private static final int CANNON_DELAY = 3000;
    private long lastCannonShot;
    private boolean shootAllowed;

    public Cannon(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x - X_OFF, y - Y_OFF, CANNON_W_DEF, CANNON_H_DEF);
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

    // Getters & setters

    public boolean isShootAllowed() {
        return shootAllowed;
    }

    public void setLastCannonShot(long lastCannonShot) {
        this.lastCannonShot = lastCannonShot;
    }

}
