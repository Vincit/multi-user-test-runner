package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;

public class UserResolver<USER, ROLE> {

    private final TestUser<ROLE> creatorModel;
    private USER creator;

    private final TestUser<ROLE> userModel;
    private USER user;

    private final UserFactory<USER, ROLE> userFactory;
    private final RoleConverter<ROLE> roleConverter;

    public UserResolver(UserFactory<USER, ROLE> userFactory, RoleConverter<ROLE> roleConverter, UserIdentifier creator, UserIdentifier user) {
        this.userFactory = userFactory;
        this.roleConverter = roleConverter;
        this.creatorModel = resolveCreatorFromIdentifier(creator);
        this.userModel = resolveUserFromIdentifier(user);
    }

    private TestUser<ROLE> resolveCreatorFromIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.USER) {
            return TestUser.forExistingUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return TestUser.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return TestUser.forRole(
                    roleConverter.stringToRole(identifier.getIdentifier()),
                    identifier
            );
        } else {
            throw new IllegalArgumentException("Invalid identifier for creator: " + identifier.getType());
        }
    }

    private TestUser<ROLE> resolveUserFromIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.CREATOR) {
            return TestUser.forCreatorUser();
        } else if (identifier.getType() == UserIdentifier.Type.NEW_USER) {
            if (creatorModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
                throw new IllegalStateException("Cannot use NEW_USER mode when creator uses existing user");
            }
            return TestUser.forNewUser(creatorModel.getRole(), identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return TestUser.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return TestUser.forRole(roleConverter.stringToRole(identifier.getIdentifier()), identifier);
        } else {
            return TestUser.forExistingUser(identifier);
        }
    }

    private void initializeUser() {
        if (userModel.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            user = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", userModel.getRole(), LoginRole.USER);
        } else if (userModel.getMode() == TestUser.RoleMode.CREATOR_USER) {
            if (creatorModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // Do nothing, resolved in getter
            } else {
                user = creator;
            }
        } else if (userModel.getMode() == TestUser.RoleMode.NEW_WITH_CREATOR_ROLE) {
            if (creatorModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // NOOP
            } else {
                user = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", creatorModel.getRole(), LoginRole.USER);
            }
        } else if (userModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, resolved in getter
        } else if (userModel.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + userModel.getMode());
        }
    }

    private void initializeCreator() {
        if (creatorModel.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            creator = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Creator", creatorModel.getRole(), LoginRole.CREATOR);
        } else if (creatorModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, resolved in getter
        } else if (creatorModel.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creatorModel.getMode());
        }
    }

    public TestUser<ROLE> getCreator() {
        return creatorModel;
    }

    public TestUser<ROLE> getUser() {
        return userModel;
    }

    public USER resolveUser() {
        if (userModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(userModel.getIdentifier());
        } else if (userModel.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else if (userModel.getMode() == TestUser.RoleMode.CREATOR_USER) {
            return resolverCreator();
        } else {
            return user;
        }
    }

    public USER resolverCreator() {
        if (creatorModel.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(creatorModel.getIdentifier());
        } else if (creatorModel.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return creator;
        }
    }

    public void resolve() {
        initializeCreator();
        initializeUser();
    }
}
