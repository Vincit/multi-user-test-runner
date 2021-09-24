package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.EmptyUserDefinitionClass;
import fi.vincit.multiusertest.rule.UserDefinitionClass;
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
    private final FocusType focusType;

    private static final AlphabeticalMergeStrategy DEFAULT_MERGE_STRATEGY = new AlphabeticalMergeStrategy();

    public static TestConfiguration fromIgnoreForUsers(Optional<IgnoreForUsers> ignoredUsers, Optional<RunWithUsers> classUsers, Class<?> runner) {
        Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        FocusType focusMode = FocusType.NONE;
        if (classUsers.isPresent() && ignoredUsers.isPresent()) {
            final RunWithUsers runWithUsers = classUsers.get();
            final IgnoreForUsers resolvedIgnoredUsers = ignoredUsers.get();
            focusMode = resolveFocusType(runWithUsers);

            final UserDefinitionClass producerDefinitionClass =
                    resolveUserDefinitionClass(runWithUsers.producerClass());
            final Collection<UserIdentifier> producerIdentifiers =
                    resolveDefinitions(
                            runWithUsers.producers(),
                            producerDefinitionClass,
                            resolvedIgnoredUsers.producers(),
                            resolveUserDefinitionClass(resolvedIgnoredUsers.producerClass()),
                            resolveFocusType(runWithUsers)
                    );

            final UserDefinitionClass consumerDefinitionClass =
                    resolveUserDefinitionClass(runWithUsers.consumerClass());
            final Collection<UserIdentifier> consumerIdentifier =
                    resolveDefinitions(
                            runWithUsers.consumers(),
                            consumerDefinitionClass,
                            resolvedIgnoredUsers.consumers(),
                            resolveUserDefinitionClass(resolvedIgnoredUsers.consumerClass()),
                            resolveFocusType(runWithUsers)
                    );

            return new TestConfiguration(
                    producerIdentifiers,
                    consumerIdentifier,
                    focusMode,
                    runner,
                    defaultException
            );
        } else {
            return new TestConfiguration(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    focusMode,
                    runner,
                    defaultException
            );
        }
    }

    private static Collection<UserIdentifier> resolveDefinitions(String[] includedUserDefinitions,
                                                                 UserDefinitionClass includedUserDefinitionsClass,
                                                                 String[] ignoredUserDefinitions,
                                                                 UserDefinitionClass ignoreUserDefinitionsClass,
                                                                 FocusType focusType) {
        final Collection<UserIdentifier> producerIdentifiers = getDefinitions(
                includedUserDefinitions,
                includedUserDefinitionsClass.getUsers(),
                DEFAULT_MERGE_STRATEGY,
                focusType
        );
        producerIdentifiers.removeAll(getDefinitions(
                ignoredUserDefinitions,
                ignoreUserDefinitionsClass
                        .getUsers(),
                DEFAULT_MERGE_STRATEGY,
                focusType
        ));
        return producerIdentifiers;
    }

    /**
     * Creates a new instance using {@link RunWithUsers} and {@link MultiUserTestConfig}
     * annotations. Uses MultiUserTestConfig for defaults.
     * @param testUsers annotation
     * @param multiUserTestConfig Optional configurations class level configuration
     * @param runner Runner class to use
     * @return Test configuration
     */
    public static TestConfiguration fromRunWithUsers(Optional<RunWithUsers> testUsers, Optional<MultiUserTestConfig> multiUserTestConfig, Class<?> runner) {

        Collection<UserIdentifier> producerIdentifiers = Collections.emptySet();
        Collection<UserIdentifier> consumerIdentifier = Collections.emptySet();
        Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        FocusType focusMode = FocusType.NONE;
        if (testUsers.isPresent()) {
            final RunWithUsers runWithUsers = testUsers.get();
            producerIdentifiers = getDefinitions(
                    runWithUsers.producers(),
                    resolveUserDefinitionClass(runWithUsers.producerClass())
                            .getUsers(),
                    DEFAULT_MERGE_STRATEGY,
                    resolveFocusType(runWithUsers)
            );
            consumerIdentifier = getDefinitions(
                    runWithUsers.consumers(),
                    resolveUserDefinitionClass(runWithUsers.consumerClass())
                            .getUsers(),
                    DEFAULT_MERGE_STRATEGY,
                    resolveFocusType(runWithUsers)
            );
            focusMode = resolveFocusType(runWithUsers);
        }
        if (multiUserTestConfig.isPresent()) {
            runner = multiUserTestConfig.get().runner();
        }

        return new TestConfiguration(
                producerIdentifiers,
                consumerIdentifier,
                focusMode,
                runner,
                defaultException
        );
    }

    private static FocusType resolveFocusType(RunWithUsers runWithUsers) {
        return runWithUsers.focusEnabled() ? FocusType.FOCUS : FocusType.NONE;
    }

    static Collection<UserIdentifier> getDefinitions(String[] definitionsPrimary,
                                                     String[] definitionsSecondary,
                                                     MergeStrategy mergeStrategy,
                                                     FocusType focusType) {
        final String[] resolvedDefinitions =
                mergeStrategy.mergeDefinitions(definitionsPrimary, definitionsSecondary);

        return Arrays.stream(resolvedDefinitions)
                .map(UserIdentifier::parse)
                .filter(m -> {
                    if (focusType == FocusType.FOCUS) {
                        return m.getFocusMode() == FocusType.FOCUS;
                    } else {
                        return true;
                    }
                })
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

    TestConfiguration(Collection<UserIdentifier> producerIdentifiers, Collection<UserIdentifier> consumerIdentifiers, FocusType focusType, Class<?> runner, Class<? extends Throwable> defaultException) {
        this.producerIdentifiers = producerIdentifiers;
        this.consumerIdentifiers = consumerIdentifiers;
        this.runner = Optional.ofNullable(runner);
        this.defaultException = Optional.ofNullable(defaultException);
        this.focusType = focusType;
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

    public FocusType getFocusType() {
        return focusType;
    }
}
