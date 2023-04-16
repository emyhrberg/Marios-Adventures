package constants;

public class ObjectConstants {

    public enum ObjectType {
        COIN_TYPE
    }

    public static int getSpriteAmount(ObjectType objectType) {
        return switch (objectType) {
            case COIN_TYPE -> 4;
        };
    }
}
