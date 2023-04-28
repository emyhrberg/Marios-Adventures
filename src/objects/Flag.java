package objects;

import constants.ObjectConstants.ObjectType;
import main.Game;

import static constants.ObjectConstants.getSpriteAmount;

public class Flag extends GameObject {

    private static final float X_OFF = 5 * Game.SCALE*2 + 5;

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
