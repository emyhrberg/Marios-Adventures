package objects;

import static constants.ObjectConstants.ObjectType;

public class Question extends GameObject {

    // ====== Hitbox =======
    private static final int HITBOX_WIDTH 	= 40;
    private static final int HITBOX_HEIGHT 	= 40;

    public Question(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(HITBOX_WIDTH, HITBOX_HEIGHT);
        doAnimation = true;
    }

    public void update() {
        updateAnimationTick();
    }
}
