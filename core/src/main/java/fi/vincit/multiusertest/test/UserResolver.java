package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;

public class UserResolver<USER, ROLE> {

    private final TestUser<ROLE> creator;
    private USER creatorUser;
    private final TestUser<ROLE> user;
    private USER userUser;
    private final UserFactory<USER, ROLE> userFactory;
    private final RoleConverter<ROLE> roleConverter;

    public UserResolver(UserFactory<USER, ROLE> userFactory, RoleConverter<ROLE> roleConverter, UserIdentifier creator, UserIdentifier user) {
        this.userFactory = userFactory;
        this.roleConverter = roleConverter;
        this.creator = resolveCreatorFromIdentifier(creator);
        this.user = resolveUserFromIdentifier(user);
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
            if (this.creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                throw new IllegalStateException("Cannot use NEW_USER mode when creator uses existing user");
            }
            return TestUser.forNewUser(creator.getRole(), identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return TestUser.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return TestUser.forRole(roleConverter.stringToRole(identifier.getIdentifier()), identifier);
        } else {
            return TestUser.forExistingUser(identifier);
        }
    }

    private void initializeUser(TestUser<ROLE> user, UserFactory<USER, ROLE> userFactory) {
        if (user.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            userUser = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", user.getRole(), LoginRole.USER);
        } else if (user.getMode() == TestUser.RoleMode.CREATOR_USER) {
            if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // Do nothing, user already set, using creator
            } else {
                userUser = creatorUser;
            }
        } else if (user.getMode() == TestUser.RoleMode.NEW_WITH_CREATOR_ROLE) {
            if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // NOOP
            } else {
                userUser = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", creator.getRole(), LoginRole.USER);
            }
        } else if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else if (user.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + user.getMode());
        }
    }

    private void initializeCreator(TestUser<ROLE> creator, UserFactory<USER, ROLE> userFactory) {
        if (creator.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            creatorUser = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Creator", creator.getRole(), LoginRole.CREATOR);
        } else if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else if (creator.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creator.getMode());
        }
    }

    public TestUser<ROLE> getCreator() {
        return creator;
    }

    public TestUser<ROLE> getUser() {
        return user;
    }

    public USER resolveUser() {
        if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(user.getIdentifier());
        } else if (user.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return userUser;
        }
    }

    public USER resolverCreator() {
        if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(creator.getIdentifier());
        } else if (creator.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return creatorUser;
        }
    }

    public void resolve() {
        initializeCreator(creator, userFactory);
        initializeUser(user, userFactory);
    }
}
