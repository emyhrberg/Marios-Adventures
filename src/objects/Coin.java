package objects;

import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.SCALE;

public class Coin extends GameObject {

    // Coin size
    public static final int COIN_SIZE_DEF = 30;
    public static final int COIN_SIZE = (int) (COIN_SIZE_DEF * SCALE);

    // Sparkle
    public static final int SPARKLE_DRAW_W = 40;
    public static final int SPARKLE_DRAW_H = 40;
    public static final int SPARKLE_W_DEF = 100;
    public static final int SPARKLE_H_DEF = 100;

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, COIN_SIZE_DEF, COIN_SIZE_DEF);
        doAnimation = true;
    }

    public void update(Coin c) {
        updateCoinAnimation(c);
    }

    private void updateCoinAnimation(Coin c) {
        animationTick++;

        // Sparkle anim
        if (c.isSparkle) {
            if (animationTick >= ANIMATION_SPEED / 2) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, remove the coin!
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
}
