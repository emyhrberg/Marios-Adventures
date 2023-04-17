package objects;

import static constants.ObjectConstants.ObjectType;

public class Platform extends GameObject {

    // ====== Hitbox =======
    private static final int HITBOX_WIDTH 	= 40;
    private static final int HITBOX_HEIGHT 	= 20;

    public Platform(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, HITBOX_WIDTH, HITBOX_HEIGHT);
        doAnimation = true;
    }

    public void update() {
        updateAnimationTick();
        updatePlatformPosition();
    }

    private final long lastCheck = System.currentTimeMillis();
    private static final int PLATFORM_MOVE_TIME = 2000;

    private void updatePlatformPosition() {
        long currentTime = System.currentTimeMillis();
        int secondsElapsed = (int) ((currentTime - lastCheck) / PLATFORM_MOVE_TIME); // Convert ms to seconds

        // Check if an even number of seconds has elapsed
        int dir = -1;
        if (secondsElapsed % 2 == 0) {
            dir = 1;
        }

        hitbox.x += dir;
    }
}
