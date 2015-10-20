package fi.vincit.multiusertest.util;

public class TestUser<ROLE> {

    public static <ROLE> TestUser<ROLE> forCreatorUser() {
        return new TestUser<>(null, RoleMode.CREATOR_USER, UserIdentifier.getCreator());
    }

    public static <ROLE> TestUser<ROLE> forNewUser(ROLE role, UserIdentifier identifier) {
        return new TestUser<>(role, RoleMode.NEW_WITH_CREATOR_ROLE, identifier);
    }

    public static <ROLE> TestUser<ROLE> forRole(ROLE role, UserIdentifier identifier) {
        return new TestUser<>(role, RoleMode.SET_USER_ROLE, identifier);
    }

    public static <ROLE> TestUser<ROLE> forExistingUser(UserIdentifier identifier) {
        return new TestUser<>(null, RoleMode.EXISTING_USER, identifier);
    }

    public static <ROLE> TestUser<ROLE> forAnonymousUser() {
        return new TestUser<>(null, RoleMode.ANONYMOUS, null);
    }

    public enum RoleMode {
        CREATOR_USER,
        NEW_WITH_CREATOR_ROLE,
        SET_USER_ROLE,
        EXISTING_USER,
        ANONYMOUS
    }

    private final ROLE role;
    private final RoleMode mode;
    private final UserIdentifier userIdentifier;


    TestUser(ROLE role, RoleMode mode, UserIdentifier userIdentifier) {
        this.role = role;
        this.mode = mode;
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


    public UserIdentifier getUserIdentifier() {
        assert userIdentifier != null : "UserIdentifier must not be null";
        return userIdentifier;
    }
    
    public String getIdentifier() {
        return getUserIdentifier().getIdentifier();
    }
}
