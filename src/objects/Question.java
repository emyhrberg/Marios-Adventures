package objects;

import constants.Direction;

import static constants.Direction.DOWN;
import static constants.Direction.UP;
import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.SCALE;

public class Question extends GameObject {

    // Size
    private static final int QUESTION_WIDTH = 40;
    private static final int QUESTION_HEIGHT = 45;

    // Pushback
    private float pushYDraw;
    private Direction pushYDir;
    private static final float BLOCK_SPEED = 1.2f * SCALE;
    private static final float BLOCK_LIMIT = -20f * SCALE;

    public Question(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, QUESTION_WIDTH, QUESTION_HEIGHT);
        doAnimation = true;
    }

    public void update() {
        updateQuestionCollision();
        updateQuestionAnimation();
    }

    private void updateQuestionAnimation() {
        animationTick++;
        if (animationTick >= ANIMATION_SPEED) {
            animationTick = 0;
            animationIndex++;
            if (animationIndex >= getSpriteAmount(objectType)) {
                animationIndex = 0;
            }
        }
    }

    private void updateQuestionCollision() {
        // Set pushBackOffsetDir to UP to start this animation
        if (pushYDir == UP) {
            isHit = true;
            pushYDraw -= BLOCK_SPEED;
            if (pushYDraw <= BLOCK_LIMIT)
                pushYDir = DOWN;
        } else {
            pushYDraw += BLOCK_SPEED;
            if (pushYDraw >= 0)
                pushYDraw = 0;
        }
    }

    // getters and setters

    public void setPushYDir(Direction pushYDir) {
        this.pushYDir = pushYDir;
    }

    public float getPushYDraw() {
        return pushYDraw;
    }
}
