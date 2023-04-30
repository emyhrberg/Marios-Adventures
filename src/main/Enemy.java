package main;

/**
 * This class handles all Enemy behavior, functionality, movement
 * Extends the Entity class for shared properties like gravity and collision
 */
public class Enemy extends Entity {

    // ====== Animations ======
    protected int animationTick, animationIndex;
    protected static final int ANIMATION_SPEED = 15;

    // ====== Constructor ======
    protected Enemy(float x, float y, float width, float height) {
        super(x, y, width, height);
    }
}
