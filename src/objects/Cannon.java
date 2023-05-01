package objects;

import constants.ObjectConstants.ObjectType;

import java.util.ArrayList;
import java.util.List;

import static constants.ObjectConstants.ObjectType.BULLET_TYPE;
import static ui.Menu.SCALE;

public class Cannon extends GameObject {

    // Size
    public static final int CANNON_W = 50;
    public static final int CANNON_H = 67;
    public static final float CANNON_SCALE = 1.3f;
    private static final float X_OFF = 19;
    private static final float Y_OFF = 6;

    // Properties
    public static final List<Bullet> bullets = new ArrayList<>();
    private long lastCannonShot;
    private boolean canShoot;
    private final int cooldown;

    public Cannon(int x, int y, ObjectType objectType, int cooldown) {
        super(x, y, objectType);
        this.cooldown = cooldown;
        initHitbox(x - X_OFF * SCALE, y - Y_OFF * SCALE, CANNON_W, CANNON_H);
    }

    public void update() {
        updateShootCooldown();
        shoot();
    }

    private void updateShootCooldown() {
        canShoot = System.currentTimeMillis() >= lastCannonShot + cooldown;

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
