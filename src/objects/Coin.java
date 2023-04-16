package objects;

import entities.Player;

import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Coin extends GameObject {

    // ====== Hitbox =======
    private static final int HITBOX_WIDTH 	= (int) (16 *2* SCALE);
    private static final int HITBOX_HEIGHT 	= (int) (16 *2* SCALE);

    public Coin(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(HITBOX_WIDTH , HITBOX_HEIGHT);
        doAnimation = true;
    }

    public void update() {
        updateAnimationTick();
    }
}
