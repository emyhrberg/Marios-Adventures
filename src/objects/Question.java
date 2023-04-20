package objects;

import constants.Direction;

import static constants.Direction.DOWN;
import static constants.Direction.UP;
import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Question extends GameObject {

    // Size
    private static final int QUESTION_WIDTH = 40;
    private static final int QUESTION_HEIGHT = 45;

    // Pushback
    private float pushDrawOffset;
    private Direction pushBackOffsetDir;
    private static final float BLOCK_SPEED = 1.2f * SCALE;
    private static final float BLOCK_LIMIT = 20f * SCALE;
    private boolean isHit = false;

    public Question(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, QUESTION_WIDTH, QUESTION_HEIGHT);
        doAnimation = true;
    }

    public boolean isHit() {
        return isHit;
    }

    public void update() {
        updateAnimationTick();
        updateQuestionCollision();
    }

    public void updateQuestionCollision() {
        // Set pushBackOffsetDir to UP to start this animation
        if (pushBackOffsetDir == UP) {
            isHit = true;
            pushDrawOffset -= BLOCK_SPEED;
            if (pushDrawOffset <= -BLOCK_LIMIT)
                pushBackOffsetDir = DOWN;
        } else {
            pushDrawOffset += BLOCK_SPEED;
            if (pushDrawOffset >= 0)
                pushDrawOffset = 0;
        }
    }

    public void setPushBackOffsetDir(Direction pushBackOffsetDir) {
        this.pushBackOffsetDir = pushBackOffsetDir;
    }

    public float getPushDrawOffset() {
        return pushDrawOffset;
    }
}
