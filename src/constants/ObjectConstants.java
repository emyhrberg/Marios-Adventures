package constants;

public class ObjectConstants {

    public enum ObjectType {
        COIN_TYPE,
        QUESTION_TYPE,
        PLATFORM_TYPE,
        LAVA_TYPE,
        SPARKLE_TYPE,
        PIPE_TYPE
    }

    public static int getSpriteAmount(ObjectType objectType) {
        return switch (objectType) {
            case COIN_TYPE, QUESTION_TYPE -> 4;
            case PLATFORM_TYPE, LAVA_TYPE, PIPE_TYPE -> 1;
            case SPARKLE_TYPE -> 7;
        };
    }
}
