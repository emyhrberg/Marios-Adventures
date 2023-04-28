package objects;

import main.Game;
import main.Player;

import static constants.ObjectConstants.ObjectType;

public class Lava extends GameObject {

    // Size
    public static final int LAVA_WIDTH_DEF = 80*3;
    public static final int LAVA_HEIGHT_DEF = 32*3;
    public static final int LAVA_WIDTH = (int) (LAVA_WIDTH_DEF * Game.SCALE);
    public static final int LAVA_HEIGHT = (int) (LAVA_HEIGHT_DEF * Game.SCALE);
    public static final int LAVA_Y_OFFSET = (int) (8*3*Game.SCALE);

    public Lava(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, LAVA_WIDTH_DEF, LAVA_HEIGHT_DEF);
    }

    public void update(Player player, Lava l) {
        if (player.getHitbox().intersects(l.hitbox)) {
            // set lava state and death
            player.setHealth(0);
            player.setInLava(true);

            // stop falling
            player.resetAirSpeed();
            player.setInAir(false);
            player.setCanJump(true);
        }
    }
}
