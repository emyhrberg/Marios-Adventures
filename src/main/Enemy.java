package main;

import constants.Direction;

import static constants.EnemyConstants.SharkAction;

/**
 * This class handles all Enemy behavior, functionality, movement
 * Extends the Entity class for shared properties like gravity and collision
 */
public class Enemy extends Entity {

    // ====== Animations ======
    protected int animationTick, animationIndex;
    protected static final int ANIMATION_SPEED = 15;

    protected boolean enemyAlive = true;
    protected SharkAction sharkAction = SharkAction.RUNNING;

    // ====== Constructor ======
    protected Enemy(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    // ====== Update enemy values ======

    protected void reduceEnemyHealth(Player player) {
        health -= 1;

        // Update push dir
        if (player.getHitbox().x < hitbox.x)
            pushXDir = Direction.RIGHT;
        else
            pushXDir = Direction.LEFT;

        // Set enemy action
        if (health <= 0)
            setAction(SharkAction.DEAD);
        else
            setAction(SharkAction.HIT);
    }

    public void resetEnemy() {
        setAction(SharkAction.RUNNING);
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
        if (direction == Direction.RIGHT)
            return width;
        else
            return 0;
    }

    public int getImageFlipWidth() {
        if (direction == Direction.RIGHT)
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
