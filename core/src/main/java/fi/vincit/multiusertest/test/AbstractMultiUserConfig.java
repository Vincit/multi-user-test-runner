package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
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

    private Authorization authorizationRule;

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
    AbstractMultiUserConfig(UserResolver<USER, ROLE> userResolver, Random random, Authorization authorizationRule) {
        this.userResolver = userResolver;
        this.random = random;
        this.authorizationRule = authorizationRule;
    }

    protected Authorization getAuthorizationRule() {
        return authorizationRule;
    }

    @Override
    public void setAuthorizationRule(Authorization authorizationRule) {
        this.authorizationRule = authorizationRule;
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
    public UserIdentifier getConsumerIdentifier() {
        return userResolver.getConsumer().getUserIdentifier();
    }

    @Override
    public USER getProducer() {
        return userResolver.resolverProducer();
    }

    @Override
    public UserIdentifier getProducerIdentifier() {
        return userResolver.getProducer().getUserIdentifier();
    }

    @Override
    public void logInAs(LoginRole role) {
        USER userToLoginWith = resolveUserToLoginWith(role);
        if (userToLoginWith != null) {
            loginWithUser(userToLoginWith);
        } else {
            loginAnonymous();
        }

        getAuthorizationRule().setRole(
                new IdentifierResolver<>(userResolver).getProducerIdentifier(),
                new IdentifierResolver<>(userResolver).getIdentifierFor(LoginRole.CONSUMER)
        );
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


    @Override
    public Class<? extends Throwable> getDefaultException(Class<?> cls) {
        TestConfiguration configuration =
                TestConfiguration.fromRunWithUsers(
                        cls.getAnnotation(RunWithUsers.class),
                        cls.getAnnotation(MultiUserTestConfig.class),
                        BlockMultiUserTestClassRunner.class
                );
        return configuration.getDefaultException()
                .orElse(Defaults.getDefaultException());
    }

    @Override
    public void initialize() {
        userResolver.resolve();
    }

    protected RoleContainer<ROLE> getConsumerRoleContainer() {
        return userResolver.getConsumer();
    }

    protected RoleContainer<ROLE> getProducerRoleContainer() {
        return userResolver.getProducer();
    }
}
