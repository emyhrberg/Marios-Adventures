package objects;

import constants.ObjectConstants.*;

import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.SCALE;

public class Flag extends GameObject {

    private static final float X_OFF = 5 * SCALE*2 + 5;

    public Flag(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x - X_OFF, y, 40,40);
    }

    public void update() {
//        updateFlagAnim();
    }

    private void updateFlagAnim() {
        animationTick++;

        // Reset animation tick and update animation index
        if (animationTick >= ANIMATION_SPEED * 1.5) {
            animationTick = 0;
            animationIndex++;

            // Reset animation index when reached all images
            if (animationIndex >= getSpriteAmount(objectType))
                animationIndex = 0;
        }

    }
}
