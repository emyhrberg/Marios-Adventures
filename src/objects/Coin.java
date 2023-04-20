package objects;

import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Coin extends GameObject {

    // Coin size
    public static final int COIN_WIDTH_ACTUAL = 30;
    public static final int COIN_HEIGHT_ACTUAL = 30;
    public static final int COIN_WIDTH = (int) (COIN_WIDTH_ACTUAL * SCALE);
    public static final int COIN_HEIGHT = (int) (COIN_HEIGHT_ACTUAL * SCALE);
    public static final int COIN_X_OFFSET = (int) (6* SCALE);
    public static final int COIN_Y_OFFSET = (int) (6* SCALE);

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x + COIN_X_OFFSET, y + COIN_Y_OFFSET, COIN_WIDTH_ACTUAL, COIN_HEIGHT_ACTUAL);
        doAnimation = true;
    }

    public void update() {
        updateAnimationTick();
    }
}
