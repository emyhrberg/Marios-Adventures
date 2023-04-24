package objects;

import main.Entity;
import main.Level;
import main.Player;

import static main.Game.SCALE;

public class HealthPowerup extends Entity {

    public static final int HEALTH_SIZE_DEF = 20;
    public static final int HEALTH_SIZE = (int) (HEALTH_SIZE_DEF * SCALE);
    public static final int HEALTH_Y_SPAWN_OFFSET = (int) (50 * SCALE);
    public static final int HEALTH_X_SPAWN_OFFSET = (int) (20 / 2 * 0.8);

    // properties
    private boolean isActive = true;

    public HealthPowerup(int x, int y) {
        super(x, y,0,0);
        initHitbox(x + HEALTH_X_SPAWN_OFFSET, y - HEALTH_Y_SPAWN_OFFSET, HEALTH_SIZE, HEALTH_SIZE);
    }

    public void update(Level level, Player player, HealthPowerup h) {
        updateHealthPos(level);
        updateHealthPickup(player, h);
    }

    private void updateHealthPickup(Player player, HealthPowerup h) {
        if (h.getHitbox().intersects(player.getHitbox())) {
            h.setActive(false);
            if (player.getHealth() < player.getMaxHealth())
                player.setHealth(player.getHealth() + 20);
        }
    }

    private void updateHealthPos(Level level) {
        // Check if player is in air and set inAir to true if he is
        if (isEntityInAir(hitbox, level))
            inAir = true;

        // Player is in air; fall to the ground
        if (inAir) {
            hitbox.y += 1;
            startFalling(level);
        } else {
            hitbox.x += 0.7f;
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
