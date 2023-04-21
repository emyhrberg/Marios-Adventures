package objects;

import static constants.ObjectConstants.ObjectType;
import static main.Game.SCALE;

public class Pipe extends GameObject {

    // Size
    public static final int PIPE_WIDTH_DEFAULT = 40;
    public static final int PIPE_HEIGHT_DEFAULT = 40;
    public static final int PIPE_WIDTH = (int) (PIPE_WIDTH_DEFAULT*2*SCALE);
    public static final int PIPE_HEIGHT = (int) (PIPE_HEIGHT_DEFAULT*2*SCALE);

    public Pipe(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, PIPE_WIDTH_DEFAULT, PIPE_HEIGHT_DEFAULT);
    }
}
