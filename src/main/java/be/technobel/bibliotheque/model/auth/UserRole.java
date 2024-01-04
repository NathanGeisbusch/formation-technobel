package be.technobel.bibliotheque.model.auth;

public enum UserRole {
    ADMIN(1),
    USER(2);

    private static final UserRole[] VALUES = UserRole.values();
    private final int value;

    public int getValue() {
        return value;
    }

    UserRole(int value) {
        this.value = value;
    }

    public static UserRole fromValue(int value) {
        for(var role : VALUES) {
            if(role.getValue() == value) return role;
        }
        return null;
    }
}
