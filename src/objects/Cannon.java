package objects;

import constants.ObjectConstants.ObjectType;
import main.Game;

import java.util.ArrayList;
import java.util.List;

import static constants.ObjectConstants.ObjectType.BULLET_TYPE;

public class Cannon extends GameObject {

    // Size
    public static final int CANNON_W_DEF = 50;
    public static final int CANNON_H_DEF = 67;
    public static final int CANNON_WIDTH = (int) (CANNON_W_DEF *1.3* Game.SCALE);
    public static final int CANNON_HEIGHT = (int) (CANNON_H_DEF *1.3*Game.SCALE);
    private static final float X_OFF = 19f * Game.SCALE;
    private static final float Y_OFF = 6f * Game.SCALE;

    // Properties
    public static final List<Bullet> bullets = new ArrayList<>();
    private static final int CANNON_DELAY = 3000;
    private long lastCannonShot;
    private boolean canShoot;

    public Cannon(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x - X_OFF, y - Y_OFF, CANNON_W_DEF, CANNON_H_DEF);
    }

    public void update() {
        updateShootCooldown();
        shoot();
    }

    private void updateShootCooldown() {
        canShoot = System.currentTimeMillis() >= lastCannonShot + CANNON_DELAY;

        if (canShoot) {
            updateCannonAnimation();
        } else if (animationIndex >= 4) {
            updateCannonAnimation();
            if (animationIndex >= 7) {
                animationIndex = 1;
            }
        }
    }

    private void shoot() {
        if (canShoot && animationIndex == 4) {
            bullets.add(new Bullet((int) hitbox.x, (int) hitbox.y, BULLET_TYPE));
            lastCannonShot = System.currentTimeMillis();
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

}
