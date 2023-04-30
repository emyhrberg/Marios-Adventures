package objects;

import helpers.SoundLoader;
import main.Game;
import main.Player;

import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.TILES_SIZE_DEFAULT;

public class Coin extends GameObject {

    // Coin size
    public static final float COIN_SIZE_DEFAULT = (int) (TILES_SIZE_DEFAULT * 0.75);
    public static final float COIN_SIZE = (int) (COIN_SIZE_DEFAULT * Game.SCALE);
    private static final float X = (TILES_SIZE_DEFAULT - COIN_SIZE_DEFAULT) / 2f * Game.SCALE;
    private static final float Y = (TILES_SIZE_DEFAULT - COIN_SIZE_DEFAULT) / 2f * Game.SCALE;

    public static int coinCount;

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x + X, y + Y, COIN_SIZE_DEFAULT, COIN_SIZE_DEFAULT);
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
                SoundLoader.playSound("/sounds/coin.wav", 0.5);
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
