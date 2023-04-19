package objects;

import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Coin extends GameObject {

    // ====== Coin size hitbox =======
    public static final int COIN_WIDTH_DEF 	    = 16;
    public static final int COIN_HEIGHT_DEF 	= 16;
    public static final int COIN_WIDTH 	        = (int) (COIN_WIDTH_DEF * 2f);
    public static final int COIN_HEIGHT 	    = (int) (COIN_HEIGHT_DEF * 2f);

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x + COIN_WIDTH_DEF / 2f, y + COIN_HEIGHT_DEF / 2f, COIN_WIDTH, COIN_HEIGHT);
        doAnimation = true;
    }

    public void update() {
        updateAnimationTick();
    }
}
