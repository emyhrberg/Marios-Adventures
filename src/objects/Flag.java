package objects;

import constants.ObjectConstants.ObjectType;

import static constants.ObjectConstants.getSpriteAmount;

public class Flag extends GameObject {

    public Flag(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, 40,40);
    }

    public void update() {
        updateFlagAnim();
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
