package main;

import helpers.SoundLoader;

import static constants.Direction.LEFT;
import static constants.Direction.RIGHT;
import static constants.EnemyConstants.SharkAction;
import static constants.EnemyConstants.SharkAction.*;
import static constants.EnemyConstants.getSharkSpriteAmount;

/**
 * This class handles all Enemy behavior, functionality, movement and extends the Entity class
 */
public class Enemy extends Entity {

    // ====== Animations ======
    protected int animationTick, animationIndex;
    protected static final int ANIMATION_SPEED = 15;

    protected boolean enemyAlive = true;
    protected SharkAction sharkAction = RUNNING;

    // ====== Constructor ======
    protected Enemy(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    // ====== Update enemy values ======

    protected void reduceEnemyHealth(Player player) {
        health -= 1;

        // Update push dir
        if (player.getHitbox().x < hitbox.x)
            pushXDir = RIGHT;
        else
            pushXDir = LEFT;

        // Set enemy action
        if (health <= 0)
            setAction(DEAD);
        else
            setAction(HIT);

        SoundLoader.playAudio("enemy_taking_damage.wav", 0.2);
    }

    public void resetEnemy() {
        setAction(RUNNING);
        hitbox.x = x;
        hitbox.y = y;
        health = maxHealth;
        enemyAlive = true;
    }

    // ====== Getters && Setters ======

    protected void setAction(SharkAction enemyAction) {
        this.sharkAction = enemyAction;

        // When setting a new action, reset the previous action
        animationTick = 0;
        animationIndex = 0;
    }

    public float getImageFlipX() {
        if (direction == RIGHT)
            return width;
        else
            return 0;
    }

    public int getImageFlipWidth() {
        if (direction == RIGHT)
            return -1;
        else
            return 1;
    }

    public boolean isEnemyAlive() {
        if (hitbox.y >= Game.GAME_HEIGHT)
            return false;
        return enemyAlive;
    }
}
