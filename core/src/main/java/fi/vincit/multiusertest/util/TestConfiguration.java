package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * Abstraction of configuration test class
 */
public class TestConfiguration {

    private final Collection<UserIdentifier> producerIdentifiers;
    private final Collection<UserIdentifier> consumerIdentifiers;
    private final Optional<Class<?>> runner;
    private final Optional<Class<? extends Throwable>> defaultException;

    /**
     * Creates a new instance using {@link RunWithUsers} and {@link MultiUserTestConfig}
     * annotations. Uses MultiUserTestConfig for defaults.
     * @param testUsers annotation
     * @param multiUserTestConfig Optional configurations class level configuration
     * @return
     */
    public static TestConfiguration fromRunWithUsers(Optional<RunWithUsers> testUsers, Optional<MultiUserTestConfig> multiUserTestConfig) {

        Collection<UserIdentifier> producerIdentifiers = Collections.emptySet();
        Collection<UserIdentifier> consumerIdentifier = Collections.emptySet();
        Class<?> runner = BlockMultiUserTestClassRunner.class;
        Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        if (testUsers.isPresent()) {
            producerIdentifiers = getDefinitions(testUsers.get().producers());
            consumerIdentifier = getDefinitions(testUsers.get().consumers());
        }
        if (multiUserTestConfig.isPresent()) {
            runner = multiUserTestConfig.get().runner();
            defaultException = multiUserTestConfig.get().defaultException();
        }

        return new TestConfiguration(
                producerIdentifiers,
                consumerIdentifier,
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

    TestConfiguration(Collection<UserIdentifier> producerIdentifiers, Collection<UserIdentifier> consumerIdentifiers, Class<?> runner, Class<? extends Throwable> defaultException) {
        this.producerIdentifiers = producerIdentifiers;
        this.consumerIdentifiers = consumerIdentifiers;
        this.runner = Optional.ofNullable(runner);
        this.defaultException = Optional.ofNullable(defaultException);
    }

    public Collection<UserIdentifier> getProducerIdentifiers() {
        return producerIdentifiers;
    }

    public Collection<UserIdentifier> getConsumerIdentifiers() {
        return consumerIdentifiers;
    }

    public Optional<Class<?>> getRunner() {
        return runner;
    }

    public Optional<Class<? extends Throwable>> getDefaultException() {
        return defaultException;
    }
}
