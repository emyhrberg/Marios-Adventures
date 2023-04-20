package objects;

import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Lava extends GameObject {

    // Size
    public static final int LAVA_WIDTH_DEF = 80*3;
    public static final int LAVA_HEIGHT_DEF = 32*3;
    public static final int LAVA_WIDTH = (int) (LAVA_WIDTH_DEF * SCALE);
    public static final int LAVA_HEIGHT = (int) (LAVA_HEIGHT_DEF * SCALE);
    public static final int LAVA_Y_OFFSET = (int) (8*3*SCALE);

    public Lava(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, LAVA_WIDTH_DEF, LAVA_HEIGHT_DEF);
    }
}
