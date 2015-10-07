package fi.vincit.multiusertest.test;

import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.Defaults;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * <p>
 * Abstract test class implementation that can be used for running MultiUserTestRunner tests
 * with a system specific configurations.
 * </p>
 *
 * @param <USER> User model type. Type of users the {@link #createUser(String, String, String, Object, LoginRole)} creates.
 * @param <ROLE> Role enum or object. Type of user roles the {@link #stringToRole(String)}.
 */
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractUserRoleIT<USER, ROLE> implements UserRoleIT<USER,ROLE> {

    private TestUser<USER, ROLE> user;
    private TestUser<USER, ROLE> creator;

    private Random random = new Random(System.currentTimeMillis());

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Before
    public void initializeUsers() {
        initializeCreator();
        initializeUser();

        authorizationRule.setExpectedException(getDefaultException());
    }

    private void initializeUser() {
        if (user.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            this.user = user.withUser(createUser(getRandomUsername(), "Test", "User", getUserRole(), LoginRole.USER));
        } else if (user.getMode() == TestUser.RoleMode.CREATOR_USER) {
            if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // Do nothing, user already set, using creator
            } else {
                this.user = user.withUser(creator.getUser());
            }
        } else if (user.getMode() == TestUser.RoleMode.NEW_WITH_CREATOR_ROLE) {
            if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // NOOP
            } else {
                this.user = user.withUser(createUser(getRandomUsername(), "Test", "User", getCreatorRole(), LoginRole.USER));
            }
        } else if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else if (user.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + user.getMode());
        }
    }

    private void initializeCreator() {
        if (creator.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            this.creator = creator.withUser(createUser(getRandomUsername(), "Test", "Creator", creator.getRole(), LoginRole.CREATOR));
        } else if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else if (creator.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creator.getMode());
        }
    }

    public AuthorizationRule authorization() {
        return authorizationRule;
    }

    @Override
    public void setUsers(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        this.creator = resolveCreatorFromIdentifier(creatorIdentifier);
        this.user = resolveUserFromIdentifier(userIdentifier);
    }

    @Override
    public USER getUser() {
        if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return getUserByUsername(user.getIdentifier());
        } else if (user.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return user.getUser();
        }
    }

    @Override
    public USER getCreator() {
        if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return getUserByUsername(creator.getIdentifier());
        } else if (creator.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return creator.getUser();
        }
    }

    protected ROLE getUserRole() {
        return user.getRole();
    }

    public ROLE getCreatorRole() {
        return creator.getRole();
    }

    @Override
    public void logInAs(LoginRole role) {
        USER userToLoginWith = resolveUserToLoginWith(role);
        if (userToLoginWith != null) {
            loginWithUser(userToLoginWith);
        } else {
            loginAnonymous();
        }

        IdentifierResolver<USER, ROLE> identifierResolver =
                new IdentifierResolver<>(user, creator);
        authorizationRule.setRole(identifierResolver.getIdentifierFor(role));
    }

    private USER resolveUserToLoginWith(LoginRole loginRole) {
        if (loginRole == LoginRole.CREATOR) {
            return getCreator();
        } else if (user.getMode() == TestUser.RoleMode.CREATOR_USER
                && creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return getCreator();
        } else {
            return getUser();
        }
    }


    /**
     * "Log in" anonymous user. By default users {@link #loginWithUser(Object)}
     * using null as the user. Can be overridden to change the behaviour.
     */
    protected void loginAnonymous() {
        loginWithUser(null);
    }

    protected String getRandomUsername() {
        return "testuser" + random.nextInt();
    }

    private TestUser<USER, ROLE> resolveCreatorFromIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.USER) {
            return TestUser.forExistingUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return TestUser.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return TestUser.forRole(
                    stringToRole(identifier.getIdentifier()),
                    identifier
            );
        } else {
            throw new IllegalArgumentException("Invalid identifier for creator: " + identifier.getType());
        }
    }

    private TestUser<USER, ROLE> resolveUserFromIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.CREATOR) {
            return TestUser.forCreatorUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.NEW_USER) {
            if (this.creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                throw new IllegalStateException("Cannot use NEW_USER mode when creator uses existing user");
            }
            return TestUser.forNewUser(getCreatorRole(), identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return TestUser.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return TestUser.forRole(stringToRole(identifier.getIdentifier()), identifier);
        } else {
            return TestUser.forExistingUser(identifier);
        }
    }

    protected TestUser<USER, ROLE> getUserModel() {
        return user;
    }

    protected TestUser<USER, ROLE> getCreatorModel() {
        return creator;
    }

    /**
     * Returns the default exception configured for the test.
     * @return Default configuration
     */
    @Override
    public Class<? extends Throwable> getDefaultException() {
        TestConfiguration configuration =
                TestConfiguration.fromTestUsers(
                        Optional.ofNullable(getClass().getAnnotation(TestUsers.class)),
                        Optional.ofNullable(getClass().getAnnotation(MultiUserTestConfig.class))
                );
        return configuration.getDefaultException()
                .orElse(Defaults.getDefaultException());
    }
}
