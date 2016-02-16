package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.util.*;

import java.util.Random;

/*
 * @deprecated Use component based configuration instead.
 */
@Deprecated
public abstract class AbstractMultiUserConfig<USER, ROLE> implements MultiUserConfig<USER, ROLE> {

    private UserResolver<USER, ROLE> userResolver;

    private Random random = new Random(System.currentTimeMillis());

    protected abstract AuthorizationRule getAuthorizationRule();

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
