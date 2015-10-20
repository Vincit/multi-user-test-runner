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
public abstract class AbstractUserRoleIT<USER, ROLE>
        implements UserRoleIT<USER>, UserFactory<USER, ROLE>, RoleConverter<ROLE> {

    private UserResolver<USER, ROLE> userResolver;

    private Random random = new Random(System.currentTimeMillis());

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Before
    public void initializeUsers() {
        userResolver.resolve();

        authorizationRule.setExpectedException(getDefaultException());
    }

    public AuthorizationRule authorization() {
        return authorizationRule;
    }

    @Override
    public void setUsers(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        userResolver = new UserResolver<>(this, this, creatorIdentifier, userIdentifier);
    }

    @Override
    public USER getUser() {
        return userResolver.resolveUser();
    }

    @Override
    public USER getCreator() {
        return userResolver.resolverCreator();
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
                new IdentifierResolver<>(userResolver.getUser(), userResolver.getCreator());
        authorizationRule.setRole(identifierResolver.getIdentifierFor(role));
    }

    private USER resolveUserToLoginWith(LoginRole loginRole) {
        if (loginRole == LoginRole.CREATOR) {
            return getCreator();
        } else if (userResolver.getUser().getMode() == TestUser.RoleMode.CREATOR_USER
                && userResolver.getCreator().getMode() == TestUser.RoleMode.EXISTING_USER) {
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

    @Override
    public String getRandomUsername() {
        return "testuser-" + random.nextInt(Integer.MAX_VALUE);
    }

    protected TestUser<ROLE> getUserModel() {
        return userResolver.getUser();
    }

    protected TestUser<ROLE> getCreatorModel() {
        return userResolver.getCreator();
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
