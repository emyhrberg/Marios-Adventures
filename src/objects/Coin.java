package objects;

import helpers.SoundPlayer;
import main.Player;

import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static ui.Menu.SCALE;
import static ui.Menu.TILES_SIZE_DEFAULT;

public class Coin extends GameObject {

    // Coin size
    public static final float COIN_SIZE = (int) (TILES_SIZE_DEFAULT * 0.75);

    public static int coinCount;

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        final float CENTER = (TILES_SIZE_DEFAULT - COIN_SIZE) / 2f * SCALE;
        initHitbox(x + CENTER, y + CENTER, COIN_SIZE, COIN_SIZE);
    }

    public void update(Player player, Coin c) {
        if (c.isActive()) {
            updateCoinAnimation();
            coinPickup(player);
        }
    }

    private void coinPickup(Player player) {
        if (hitbox.intersects(player.getHitbox()))
            if (!isSparkle) {
                isSparkle = true;
                SoundPlayer.playSound("/sounds/coin.wav");
                coinCount++;
            }
    }

    private void updateCoinAnimation() {
        animationTick++;

        // Sparkle anim
        if (isSparkle) {
            if (animationTick >= ANIMATION_SPEED / 2) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, remove the coin!
            if (animationIndex == 7)
                active = false;
        // Coin anim
        } else {
            if (animationTick >= ANIMATION_SPEED) {
                animationTick = 0;
                animationIndex++;
                if (animationIndex >= getSpriteAmount(objectType)) {
                    animationIndex = 0;
                }
            }
        }
    }
}
