package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.util.*;

import java.util.Random;

/**
 * Default configuration base class for multi user tests. Authorization rule is automatically set
 * by the runner.
 * @param <USER> User type
 * @param <ROLE> Role type
 */
public abstract class AbstractMultiUserConfig<USER, ROLE> implements MultiUserConfig<USER, ROLE> {

    private UserResolver<USER, ROLE> userResolver;

    private Random random = new Random(System.currentTimeMillis());

    private AuthorizationRule authorizationRule;

    /**
     * Default constructor
     */
    public AbstractMultiUserConfig() {
    }

    /**
     * Constructor for tests
     * @param userResolver User resolver
     * @param random Random number generator
     * @param authorizationRule Authorization rule
     */
    AbstractMultiUserConfig(UserResolver<USER, ROLE> userResolver, Random random, AuthorizationRule authorizationRule) {
        this.userResolver = userResolver;
        this.random = random;
        this.authorizationRule = authorizationRule;
    }

    protected AuthorizationRule getAuthorizationRule() {
        return authorizationRule;
    }

    @Override
    public void setAuthorizationRule(AuthorizationRule authorizationRule, Object testClassInstance) {
        this.authorizationRule = authorizationRule;
        this.authorizationRule.setExpectedException(getDefaultException(testClassInstance.getClass()));
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

        getAuthorizationRule().setRole(new IdentifierResolver<>(userResolver).getIdentifierFor(role));
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
        TestConfiguration configuration =
                TestConfiguration.fromRunWithUsers(
                        Optional.ofNullable(cls.getAnnotation(RunWithUsers.class)),
                        Optional.ofNullable(cls.getAnnotation(MultiUserTestConfig.class))
                );
        return configuration.getDefaultException()
                .orElse(Defaults.getDefaultException());
    }

    @Override
    public void initialize() {
        userResolver.resolve();
    }
}
