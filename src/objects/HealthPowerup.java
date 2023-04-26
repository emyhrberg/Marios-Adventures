package objects;

import helpers.SoundLoader;
import main.Entity;
import main.Level;
import main.Player;

import static main.Game.*;
import static objects.ObjectManager.healths;

public class HealthPowerup extends Entity {

    public static final int HEALTH_SIZE_DEF = 20;
    public static final int HEALTH_SIZE = (int) (HEALTH_SIZE_DEF * SCALE);
    public static final int HEALTH_Y_SPAWN_OFFSET = (int) (50 * SCALE);
    public static final int HEALTH_X_SPAWN_OFFSET = (int) (20 / 2 * 0.8);

    // properties
    private boolean isActive = true;
    private static final float X_SPEED = 0.4f * SCALE;
    private static final float Y_SPEED = 0.3f * SCALE;

    public HealthPowerup(int x, int y) {
        super(x, y,0,0);
        initHitbox(x + HEALTH_X_SPAWN_OFFSET, y - HEALTH_Y_SPAWN_OFFSET, HEALTH_SIZE, HEALTH_SIZE);
    }

    public void update(Level level, Player player, HealthPowerup h) {
        if (h.isActive) {
            updateHealthPos(level, h);
            updateHealthPickup(player, h);
        }
    }

    private void updateHealthPickup(Player player, HealthPowerup h) {
        if (h.getHitbox().intersects(player.getHitbox())) {
            h.setActive(false);

            // increase health if applicable
            player.setHealth(player.getHealth() + 1);
            SoundLoader.playAudio("/audio/powerup.wav", 0.7);
        }
    }

    private void updateHealthPos(Level level, HealthPowerup h) {
        // Check if player is in air and set inAir to true if he is
        if (isEntityInAir(hitbox, level))
            inAir = true;

        // Player is in air; fall to the ground
        if (inAir) {
            hitbox.y += Y_SPEED;
            startFalling(level);
        } else {
            hitbox.x += X_SPEED;
        }
    }

    // getters

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
