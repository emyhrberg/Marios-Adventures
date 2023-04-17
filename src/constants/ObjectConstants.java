package constants;

public class ObjectConstants {

    public enum ObjectType {
        COIN_TYPE,
        QUESTION_TYPE,
        PLATFORM_TYPE
    }

    public static int getSpriteAmount(ObjectType objectType) {
        return switch (objectType) {
            case COIN_TYPE, QUESTION_TYPE -> 4;
            case PLATFORM_TYPE -> 1;
        };
    }
}
