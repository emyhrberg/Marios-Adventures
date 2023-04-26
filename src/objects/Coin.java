package objects;

import helpers.SoundLoader;
import main.Player;

import static constants.ObjectConstants.ObjectType;
import static constants.ObjectConstants.getSpriteAmount;
import static main.Game.SCALE;
import static main.Game.TILES_SIZE_DEFAULT;

public class Coin extends GameObject {

    // Coin size
    public static final int COIN_SIZE_DEFAULT = (int) (TILES_SIZE_DEFAULT * 0.75);
    public static final int COIN_SIZE = (int) (COIN_SIZE_DEFAULT * SCALE);
    private static final int X = (int) ((TILES_SIZE_DEFAULT - COIN_SIZE_DEFAULT) / 2 * SCALE);
    private static final int Y = (int) ((TILES_SIZE_DEFAULT - COIN_SIZE_DEFAULT) / 2 * SCALE);

    public static int coinCount;

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x+X, y+Y, COIN_SIZE_DEFAULT, COIN_SIZE_DEFAULT);
    }

    public void update(Player player, Coin c) {
        if (c.isActive()) {
            updateCoinAnimation(c);
            coinPickup(player, c);
        }
    }

    private void coinPickup(Player player,Coin c) {
        if (c.hitbox.intersects(player.getHitbox())) {
            if (!c.isSparkle()) {
                c.setSparkle(true);
                SoundLoader.playAudio("/audio/coin.wav", 0.5);
                coinCount++;
            }
        }
    }

    private void updateCoinAnimation(Coin c) {
        animationTick++;

        // Sparkle anim
        if (c.isSparkle) {
            if (animationTick >= ANIMATION_SPEED / 2) {
                animationTick = 0;
                animationIndex++;
            }
            // at final sparkle image, remove the coin!
            if (animationIndex == 7) {
                c.setActive(false);
            }

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
