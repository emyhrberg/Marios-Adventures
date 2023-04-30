package objects;

import main.Game;

import static constants.ObjectConstants.ObjectType;

public class Pipe extends GameObject {

    // Size
    public static final int PIPE_W = Game.TILES_SIZE_DEFAULT;
    public static final int PIPE_H = Game.TILES_SIZE_DEFAULT;

    public Pipe(int x, int y, ObjectType objectType) {
        super(x, y, objectType);
        initHitbox(x, y, PIPE_W * 2, PIPE_H * 2);
    }
}
