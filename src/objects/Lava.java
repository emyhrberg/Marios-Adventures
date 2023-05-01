package objects;

import main.Player;

import static constants.ObjectConstants.ObjectType;

public class Lava extends GameObject {

    // Size
    public static final int LAVA_W = 80*3;
    public static final int LAVA_H = 64*3;
    public static final int LAVA_Y_OFF = 10*3;

    public Lava(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, LAVA_W, LAVA_H);
    }

    public void update(Player player, Lava l) {
        if (player.getHitbox().intersects(l.hitbox)) {
            // set lava state and death
            player.setHealth(0);
            player.setInLava(true);
            player.setCanJump(true);
        }
    }
}
