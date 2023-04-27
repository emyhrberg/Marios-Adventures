package objects;

import constants.ObjectConstants.ObjectType;
import main.Player;

import java.util.Random;

import static constants.ObjectConstants.ObjectType.BULLET_TYPE;
import static main.Game.SCALE;
import static objects.ObjectManager.bullets;

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
    private boolean canShoot;

    public Cannon(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x - X_OFF, y - Y_OFF, CANNON_W_DEF, CANNON_H_DEF);
    }

    public void update(Player player, Cannon c) {
        updateShootCooldown();
        shoot(c);
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

    private void shoot(Cannon c) {
        if (canShoot && animationIndex == 4) {
            // Add a bullet
            bullets.add(new Bullet((int) c.hitbox.x, (int) c.hitbox.y, BULLET_TYPE));
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
