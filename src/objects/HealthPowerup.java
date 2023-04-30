package objects;

import helpers.SoundLoader;
import main.Entity;
import main.Game;
import main.Level;
import main.Player;

import static main.Game.TILES_SIZE;
import static main.Game.TILES_SIZE_DEFAULT;
import static main.Level.solidTiles;
import static objects.Question.lastBoxY;

public class HealthPowerup extends Entity {

    public static final float HEALTH_SIZE = TILES_SIZE_DEFAULT * 0.65f;

    // properties
    private boolean isActive = true;
    private boolean spawnMove = true;
    private static final float X_SPEED = 0.40f * Game.SCALE;
    private static final float Y_SPEED = 0.25f * Game.SCALE;
    private static final float Y_SPEED_SPAWN = 0.15f * Game.SCALE;

    public HealthPowerup(float x, float y) {
        super(x, y, 0, 0);
        float xOff = (TILES_SIZE_DEFAULT - HEALTH_SIZE) / 2f * Game.SCALE;
        initHitbox(x + xOff, y, HEALTH_SIZE * Game.SCALE, HEALTH_SIZE * Game.SCALE);
    }

    public void update(Player player, Level level, HealthPowerup h) {
        if (h.isActive) {
            updateHealthPos(level, h);
            updateHealthPickup(player, h);
        }
    }

    private void updateHealthPickup(Player player, HealthPowerup h) {
        if (h.getHitbox().intersects(player.getHitbox())) {
            // increase health and disable item
            player.setHealth(player.getHealth() + 1);
            h.setActive(false);
            SoundLoader.playSound("/sounds/powerup.wav", 0.7);
        }
    }

    private void updateHealthPos(Level level, HealthPowerup h) {
        if (spawnMove) {
            // keep moving the powerup until distance has gone up to above the question-mark, at level height
            float dist = Math.abs(lastBoxY - hitbox.y);
            if (dist <= hitbox.height) {
                hitbox.y -= Y_SPEED_SPAWN;
                return; // exit and keep loop-moving down
            } else {
                spawnMove = false;
            }
        }

        // Stop moving if hit a tile to the right
        if (hitSolidTileRight(level))
            return;

        // Check if player is in air and set inAir to true if he is
        if (isEntityInAir(hitbox, level))
            inAir = true;

        // Player is in air; fall to the ground
        if (inAir) {
            h.getHitbox().y += Y_SPEED; // move down
            startFalling(level); // stop falling when hit the ground
        } else {
            h.getHitbox().x += X_SPEED; // move right
        }
    }

    private boolean hitSolidTileRight(Level level) {
        int tileX = (int) ((hitbox.x - hitbox.width / 2) / TILES_SIZE);
        int tileY = (int) (hitbox.y / TILES_SIZE);

        int distanceToTile = 0;

        if (isTileOutsideLevel(tileY)) return false;

        return solidTiles.contains(level.getLevelData()[tileY][tileX + 1 + distanceToTile]);
    }

    // getters

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
