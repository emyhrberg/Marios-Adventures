package objects;

import constants.Direction;
import helpers.Sound;
import main.Player;

import static constants.Direction.DOWN;
import static constants.Direction.UP;
import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static objects.ObjectManager.healths;
import static ui.Menu.SCALE;

public class Question extends GameObject {

    // Size
    private static final int QUESTION_WIDTH = 40;
    private static final int QUESTION_HEIGHT = 45;

    // Pushback
    private float pushYDraw;
    private Direction pushYDir;
    private static final float BLOCK_SPEED = 1.25f;
    private static final float BLOCK_LIMIT = -30f;

    public Question(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, QUESTION_WIDTH, QUESTION_HEIGHT);
    }

    public void update(Player player, Question q) {
        updatePlayerQuestionCollision(player, q);
        updateQuestionBounce();
        updateQuestionAnimation();
    }

    private long lastCollision;
    private static final int POWERUP_SPAWN_DELAY = 200;
    public static float lastBoxY;

    private void updatePlayerQuestionCollision(Player player, Question q) {
        if (q.hitbox.intersects(player.getHitbox()) && player.getAirSpeed() < 0) {
            // Question collision first time!
            if (!q.isHit()) {
                q.setHit(true);
                q.lastCollision = System.currentTimeMillis();
            }

            // Set question bounce to up and player goes down
            q.pushYDir = UP;
            player.resetAirSpeed();
            Sound.play("/sounds/question.wav");
        }

        boolean timePassed = System.currentTimeMillis() - q.lastCollision >= POWERUP_SPAWN_DELAY;
        if (lastCollision != 0 && timePassed && !q.spawn && q.isHit) {
            healths.add(new HealthPowerup(hitbox.x, hitbox.y));
            lastBoxY = hitbox.y;
            q.spawn = true;
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
            pushYDraw -= BLOCK_SPEED * SCALE;
            if (pushYDraw <= BLOCK_LIMIT * SCALE)
                pushYDir = DOWN;
        } else {
            pushYDraw += BLOCK_SPEED * SCALE;
            if (pushYDraw >= 0)
                pushYDraw = 0;
        }
    }

    // getters and setters

    public float getPushYDraw() {
        return pushYDraw;
    }
}
