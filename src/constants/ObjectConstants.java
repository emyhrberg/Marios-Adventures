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
        POWERUP_HEALTH_TYPE,
        FLAG_TYPE
    }

    public static int getSpriteAmount(ObjectType objectType) {
        int result = 1;
        switch (objectType) {
            case PLATFORM_TYPE:
            case LAVA_TYPE:
            case PIPE_TYPE:
            case BRICK_TYPE:
            case POWERUP_HEALTH_TYPE:
                result = 1;
                break;
            case COIN_TYPE:
            case QUESTION_TYPE:
                result = 4;
                break;
            case CANNON_TYPE:
                result = 7;
                break;
            case BULLET_TYPE:
            case SPARKLE_TYPE:
                result = 8;
                break;
            case FLAG_TYPE:
                result = 3;
                break;
            default:
                // Handle the default case here
                // Set a default value for 'result' if needed
                break;
        }
        return result;

    }
}
