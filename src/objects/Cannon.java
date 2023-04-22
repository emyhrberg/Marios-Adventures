package objects;

import constants.ObjectConstants.ObjectType;

import static main.Game.SCALE;
import static main.Game.UPS;

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
        updateCooldown();
        if (doAnimation)
            updateCannonAnimation();
    }

    private void updateCannonAnimation() {
        animationTick++;

        if (animationTick >= ANIMATION_SPEED * 2) {
            animationTick = 0;
            animationIndex++;

            if (animationIndex >= 7) {
                doAnimation = false;
                animationIndex = 1;
            }
        }
    }

    protected int cooldownLimit = UPS*5; // 5 seconds
    protected int cooldownTick;
    private long previousTime = System.currentTimeMillis();

    protected void updateCooldown() {
        long currentTime = System.currentTimeMillis();
        int elapsedUpdates = (int) ((currentTime - previousTime) / (1000.0f / UPS));
        previousTime = currentTime;
        cooldownTick += elapsedUpdates;
        System.out.println(cooldownTick);
        if (cooldownTick > cooldownLimit) {
            doAnimation = true;
            cooldownTick = 0;
        }
    }


}
