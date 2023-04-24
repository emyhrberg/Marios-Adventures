package objects;

import constants.ObjectConstants.ObjectType;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static main.Game.SCALE;

public class Brick extends GameObject {

    // size
    public static final int BRICK_W_D = 40;
    public static final int BRICK_H_D = 40;
    public static final int BRICK_W = (int) (BRICK_W_D * SCALE);
    public static final int BRICK_H = (int) (BRICK_H_D * SCALE);

    public Brick(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        float off = 2 * SCALE;

        initHitbox(x + off, y + BRICK_H - off, BRICK_W_D - off, 10);
    }

    public void update(Brick b) {
        updateBrickAnimation(b);
    }

    private void updateBrickAnimation(Brick b) {
        animationTick++;

        // Sparkle anim
        if (isSparkle) {
            if (animationTick >= ANIMATION_SPEED / 2) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, disable the animation!
            if (animationIndex == 7) {
                b.setSparkle(false);
            }
        }
    }

}
