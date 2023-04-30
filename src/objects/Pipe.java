package objects;

import static constants.ObjectConstants.ObjectType;

public class Pipe extends GameObject {

    // Size
    public static final int PIPE_W = 40;
    public static final int PIPE_H = 40;
    public static final float PIPE_SCALE = 2f;

    public Pipe(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, PIPE_W * PIPE_SCALE, PIPE_H * PIPE_SCALE);
    }
}
