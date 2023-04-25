package objects;

import constants.Direction;
import helpers.SoundLoader;
import main.Player;

import static constants.Direction.DOWN;
import static constants.Direction.UP;
import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.SCALE;
import static objects.ObjectManager.healths;

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

    public void update(Player player, Question q) {
        updatePlayerQuestionCollision(player, q);
        updateQuestionBounce();
        updateQuestionAnimation();
    }

    private void updatePlayerQuestionCollision(Player player, Question q) {
        // handle player question collision
        if (q.hitbox.intersects(player.getHitbox()) && player.getAirSpeed() < 0) {

            // Set question bounce to up and player down
            q.pushYDir = UP;
            player.setAirSpeed(0);

            // Question collision first time!
            if (!q.isHit()) {
                q.setHit(true);
                healths.add(new HealthPowerup((int) q.hitbox.x, (int) q.hitbox.y));
            }
            SoundLoader.playAudio("/audio/question.wav");
        }
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

    private void updateQuestionBounce() {
        // Set pushBackOffsetDir to UP to start this animation
        if (pushYDir == UP) {
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

    public float getPushYDraw() {
        return pushYDraw;
    }
}
