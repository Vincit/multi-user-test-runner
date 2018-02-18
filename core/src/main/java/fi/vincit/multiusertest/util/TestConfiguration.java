package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.EmptyUserDefinitionClass;
import fi.vincit.multiusertest.rule.UserDefinitionClass;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.merge.AlphabeticalMergeStrategy;
import fi.vincit.multiusertest.util.merge.MergeStrategy;

import java.util.*;

import static java.util.stream.Collectors.toCollection;

/**
 * Abstraction of configuration test class
 */
public class TestConfiguration {

    private final Collection<UserIdentifier> producerIdentifiers;
    private final Collection<UserIdentifier> consumerIdentifiers;
    private final Optional<Class<?>> runner;
    private final Optional<Class<? extends Throwable>> defaultException;

    private static final AlphabeticalMergeStrategy DEFAULT_MERGE_STRATEGY = new AlphabeticalMergeStrategy();

    public static TestConfiguration fromIgnoreForUsers(Optional<IgnoreForUsers> ignoredUsers, Optional<RunWithUsers> classUsers) {

        Class<?> runner = BlockMultiUserTestClassRunner.class;
        Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        if (classUsers.isPresent() && ignoredUsers.isPresent()) {
            final RunWithUsers runWithUsers = classUsers.get();
            final UserDefinitionClass producerDefinitionClass =
                    resolveUserDefinitionClass(runWithUsers.producerClass());
            final Collection<UserIdentifier> producerIdentifiers = getDefinitions(
                    runWithUsers.producers(),
                    producerDefinitionClass.getUsers(),
                    DEFAULT_MERGE_STRATEGY
            );
            producerIdentifiers.removeAll(getDefinitions(
                    ignoredUsers.get().producers(),
                    resolveUserDefinitionClass(ignoredUsers.get().producerClass())
                            .getUsers(),
                    DEFAULT_MERGE_STRATEGY
            ));

            final UserDefinitionClass consumerDefinitionClass =
                    resolveUserDefinitionClass(runWithUsers.consumerClass());
            final Collection<UserIdentifier> consumerIdentifier = getDefinitions(
                    runWithUsers.consumers(),
                    consumerDefinitionClass.getUsers(),
                    DEFAULT_MERGE_STRATEGY
            );
            consumerIdentifier.removeAll(getDefinitions(
                    ignoredUsers.get().consumers(),
                    resolveUserDefinitionClass(ignoredUsers.get().consumerClass())
                            .getUsers(),
                    DEFAULT_MERGE_STRATEGY
            ));

            return new TestConfiguration(
                    producerIdentifiers,
                    consumerIdentifier,
                    runner,
                    defaultException
            );
        } else {
            return new TestConfiguration(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    runner,
                    defaultException
            );
        }
    }

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
            final RunWithUsers runWithUsers = testUsers.get();
            producerIdentifiers = getDefinitions(
                    runWithUsers.producers(),
                    resolveUserDefinitionClass(runWithUsers.producerClass())
                            .getUsers(),
                    DEFAULT_MERGE_STRATEGY
            );
            consumerIdentifier = getDefinitions(
                    runWithUsers.consumers(),
                    resolveUserDefinitionClass(runWithUsers.consumerClass())
                            .getUsers(),
                    DEFAULT_MERGE_STRATEGY
            );
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

    static Collection<UserIdentifier> getDefinitions(String[] definitionsPrimary,
                                                     String[] definitionsSecondary,
                                                     MergeStrategy mergeStrategy) {
        final String[] resolvedDefinitions =
                mergeStrategy.mergeDefinitions(definitionsPrimary, definitionsSecondary);

        return Arrays.stream(resolvedDefinitions)
                        .map(UserIdentifier::parse)
                        .collect(toCollection(LinkedHashSet::new));
    }


    static UserDefinitionClass resolveUserDefinitionClass(Class<? extends UserDefinitionClass> userClass) {
        if (userClass != null) {
            try {
                return userClass.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                throw new IllegalArgumentException("Invalid user definition class", e);
            }
        } else {
            return EmptyUserDefinitionClass.getEmptyClass();
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
