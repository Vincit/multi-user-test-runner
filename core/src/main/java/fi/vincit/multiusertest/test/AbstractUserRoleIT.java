package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.Random;

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
    public void setUsers(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        userResolver = new UserResolver<>(this, this, producerIdentifier, consumerIdentifier);
    }

    @Override
    public USER getConsumer() {
        return userResolver.resolveConsumer();
    }

    @Override
    public USER getProducer() {
        return userResolver.resolverProducer();
    }

    @Override
    public void logInAs(LoginRole role) {
        USER userToLoginWith = resolveUserToLoginWith(role);
        if (userToLoginWith != null) {
            loginWithUser(userToLoginWith);
        } else {
            loginAnonymous();
        }

        authorizationRule.setRole(new IdentifierResolver<>(userResolver).getIdentifierFor(role));
    }

    private USER resolveUserToLoginWith(LoginRole loginRole) {
        if (loginRole == LoginRole.PRODUCER) {
            return getProducer();
        } else {
            return getConsumer();
        }
    }

    @Override
    public void loginAnonymous() {
        loginWithUser(null);
    }

    @Override
    public String getRandomUsername() {
        return "testuser-" + random.nextInt(Integer.MAX_VALUE);
    }

    protected RoleContainer<ROLE> getConsumerModel() {
        return userResolver.getConsumer();
    }

    protected RoleContainer<ROLE> getProducerModel() {
        return userResolver.getProducer();
    }

    /**
     * Returns the default exception configured for the test.
     * @return Default configuration
     */
    @Override
    public Class<? extends Throwable> getDefaultException() {
        return getDefaultException(getClass());
    }

    @Override
    public Class<? extends Throwable> getDefaultException(Class<?> cls) {
        TestConfiguration configuration;
        if (cls.getAnnotation(TestUsers.class) != null) {
            configuration =
                    TestConfiguration.fromTestUsers(
                            Optional.ofNullable(cls.getAnnotation(TestUsers.class)),
                            Optional.ofNullable(cls.getAnnotation(MultiUserTestConfig.class))
                    );
        } else {
            configuration =
                    TestConfiguration.fromRunWithUsers(
                            Optional.ofNullable(cls.getAnnotation(RunWithUsers.class)),
                            Optional.ofNullable(cls.getAnnotation(MultiUserTestConfig.class))
                    );
        }
        return configuration.getDefaultException()
                .orElse(Defaults.getDefaultException());
    }
}
