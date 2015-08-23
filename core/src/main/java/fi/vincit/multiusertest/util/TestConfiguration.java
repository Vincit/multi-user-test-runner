package fi.vincit.multiusertest.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;

public class TestConfiguration {

    private final Collection<UserIdentifier> creatorIdentifiers;
    private final Collection<UserIdentifier> userIdentifiers;
    private final Optional<Class<?>> runner;
    private final Optional<Class<? extends Throwable>> defaultException;

    public static TestConfiguration fromTestUsers(TestUsers testUsers, Optional<MultiUserTestConfig> multiUserTestConfig) {

        // TODO: Null handling to Optional
        Collection<UserIdentifier> creatorIdentifiers = Collections.emptySet();
        Collection<UserIdentifier> userIdentifiers = Collections.emptySet();
        Class<?> runner = BlockMultiUserTestClassRunner.class;
        Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        if (testUsers != null) {
            creatorIdentifiers = getDefinitions(testUsers.creators());
            userIdentifiers = getDefinitions(testUsers.users());
        }
        if (multiUserTestConfig.isPresent()) {
            runner = multiUserTestConfig.get().runner();
            defaultException = multiUserTestConfig.get().defaultException();
        }

        return new TestConfiguration(
                creatorIdentifiers,
                userIdentifiers,
                runner,
                defaultException
        );
    }

    private static Collection<UserIdentifier> getDefinitions(String[] definitions) {
        if (definitions != null) {
            Collection<UserIdentifier> userIdentifiers = new LinkedHashSet<>();
            for (String user : definitions) {
                userIdentifiers.add(UserIdentifier.parse(user));
            }
            return userIdentifiers;
        } else {
            return Collections.emptySet();
        }
    }

    TestConfiguration() {
        this.creatorIdentifiers = Collections.emptySet();
        this.userIdentifiers = Collections.emptySet();
        this.runner = Optional.empty();
        this.defaultException = Optional.empty();
    }

    TestConfiguration(Collection<UserIdentifier> creatorIdentifiers, Collection<UserIdentifier> userIdentifiers, Class<?> runner, Class<? extends Throwable> defaultException) {
        this.creatorIdentifiers = creatorIdentifiers;
        this.userIdentifiers = userIdentifiers;
        this.runner = Optional.<Class<?>>ofNullable(runner);
        this.defaultException = Optional.<Class<? extends Throwable>>ofNullable(defaultException);
    }

    public Collection<UserIdentifier> getCreatorIdentifiers() {
        return creatorIdentifiers;
    }

    public Collection<UserIdentifier> getUserIdentifiers() {
        return userIdentifiers;
    }

    public Optional<Class<?>> getRunner() {
        return runner;
    }

    public Optional<Class<? extends Throwable>> getDefaultException() {
        return defaultException;
    }
}
