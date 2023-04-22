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

    public Cannon(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, CANNON_W_DEF, CANNON_H_DEF);
    }

    public void update() {
        updateCannonAnimation();
    }

    private void updateCannonAnimation() {
        animationTick++;

        // Reset animation tick and update animation index
        if (animationTick >= ANIMATION_SPEED * 4) {
            animationTick = 0;
            animationIndex++;

            // Reset animation index when reached all images
            if (animationIndex >= 7)
                animationIndex = 0;
        }
    }
}
