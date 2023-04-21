package objects;

import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.SCALE;

public class Coin extends GameObject {

    // Coin size
    public static final int COIN_WIDTH_ACTUAL = 30;
    public static final int COIN_HEIGHT_ACTUAL = 30;
    public static final int COIN_WIDTH = (int) (COIN_WIDTH_ACTUAL * SCALE);
    public static final int COIN_HEIGHT = (int) (COIN_HEIGHT_ACTUAL * SCALE);
    public static final int COIN_X_OFFSET = (int) (6* SCALE);
    public static final int COIN_Y_OFFSET = (int) (6* SCALE);

    // Sparkle
    private boolean isSparkle = false;
    public static final int SPARKLE_DRAW_W = 40;
    public static final int SPARKLE_DRAW_H = 40;
    public static final int SPARKLE_ACTUAL_W = 100;
    public static final int SPARKLE_ACTUAL_H = 100;


    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x + COIN_X_OFFSET, y + COIN_Y_OFFSET, COIN_WIDTH_ACTUAL, SPARKLE_DRAW_W);
        doAnimation = true;
    }

    public void update(Coin c) {
        updateCoinAnimation(c);
    }

    private void updateCoinAnimation(Coin c) {
        animationTick++;

        // Sparkle anim
        if (isSparkle) {
            if (animationTick >= ANIMATION_SPEED / 1.5) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, disable the coin!
            if (animationIndex == 7) {
                c.setActive(false);
            }

        // Coin anim
        } else {
            if (animationTick >= ANIMATION_SPEED) {
                animationTick = 0;
                animationIndex++;
                if (animationIndex >= getSpriteAmount(objectType)) {
                    animationIndex = 0;
                }
            }
        }
    }

    public boolean isSparkle() {
        return isSparkle;
    }

    public void setSparkle(boolean sparkle) {
        this.isSparkle = sparkle;
    }
}
