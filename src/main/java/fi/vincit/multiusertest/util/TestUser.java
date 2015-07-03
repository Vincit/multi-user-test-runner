package fi.vincit.multiusertest.util;

public class TestUser<USER, USER_ID, ROLE> {

    public static <USER, USER_ID, ROLE> TestUser<USER, USER_ID, ROLE> forCreatorUser(UserIdentifier identifier) {
        return new TestUser<>(null, RoleMode.CREATOR_USER, null, identifier);
    }

    public static <USER, USER_ID, ROLE> TestUser<USER, USER_ID, ROLE> forNewUser(ROLE role, UserIdentifier identifier) {
        return new TestUser<>(role, RoleMode.NEW_WITH_CREATOR_ROLE, null, identifier);
    }

    public static <USER, USER_ID, ROLE> TestUser<USER, USER_ID, ROLE> forRole(ROLE role, UserIdentifier identifier) {
        return new TestUser<>(role, RoleMode.SET_USER_ROLE, null, identifier);
    }

    public static <USER, USER_ID, ROLE> TestUser<USER, USER_ID, ROLE> forExistingUser(UserIdentifier identifier) {
        return new TestUser<>(null, RoleMode.EXISTING_USER, null, identifier);
    }

    public enum RoleMode {
        CREATOR_USER,
        NEW_WITH_CREATOR_ROLE,
        SET_USER_ROLE,
        EXISTING_USER
    }

    private ROLE role;
    private RoleMode mode;
    private USER user;
    private UserIdentifier userIdentifier;


    public TestUser(ROLE role, RoleMode mode, USER user, UserIdentifier userIdentifier) {
        this.role = role;
        this.mode = mode;
        this.user = user;
        this.userIdentifier = userIdentifier;
    }

    public ROLE getRole() {
        assert role != null : "Role must not be null";
        return role;
    }

    public RoleMode getMode() {
        assert mode != null : "Mode must not be null";
        return mode;
    }

    public USER getUser() {
        assert user != null : "User must not be null";
        return user;
    }

    public TestUser<USER, USER_ID, ROLE> withUser(USER user) {
        return new TestUser<>(this.role, this.mode, user, this.userIdentifier);
    }

    public UserIdentifier getUserIdentifier() {
        assert userIdentifier != null : "UserIdentifier must not be null";
        return userIdentifier;
    }

    public String getIdentifier() {
        return getUserIdentifier().getIdentifier();
    }
}
