package constants;

public class ObjectConstants {

    public enum ObjectType {
        COIN_TYPE,
        QUESTION_TYPE,
        PLATFORM_TYPE,
        LAVA_TYPE,
        SPARKLE_TYPE,
        PIPE_TYPE,
        CANNON_TYPE,
        BULLET_TYPE,
        BRICK_TYPE,
        POWERUP_HEALTH_TYPE
    }

    public static int getSpriteAmount(ObjectType objectType) {
        return switch (objectType) {
            case PLATFORM_TYPE, LAVA_TYPE, PIPE_TYPE, BRICK_TYPE, POWERUP_HEALTH_TYPE -> 1;
            case COIN_TYPE, QUESTION_TYPE -> 4;
            case CANNON_TYPE -> 7;
            case BULLET_TYPE, SPARKLE_TYPE -> 8;
        };
    }
}
