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
    private final Class<?> runner;
    private final Class<? extends Throwable> defaultException;
    private final FocusType focusType;

    private static final AlphabeticalMergeStrategy DEFAULT_MERGE_STRATEGY = new AlphabeticalMergeStrategy();

    public static TestConfiguration fromIgnoreForUsers(IgnoreForUsers ignoredUsers, RunWithUsers classUsers, Class<?> runner) {
        final Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        if (classUsers != null && ignoredUsers != null) {
            final UserDefinitionClass producerDefinitionClass =
                    resolveUserDefinitionClass(classUsers.producerClass());
            final FocusType focusType = resolveFocusType(classUsers);
            final Collection<UserIdentifier> producerIdentifiers =
                    resolveDefinitions(
                            classUsers.producers(),
                            producerDefinitionClass,
                            ignoredUsers.producers(),
                            resolveUserDefinitionClass(ignoredUsers.producerClass()),
                            focusType
                    );

            final UserDefinitionClass consumerDefinitionClass =
                    resolveUserDefinitionClass(classUsers.consumerClass());
            final Collection<UserIdentifier> consumerIdentifier =
                    resolveDefinitions(
                            classUsers.consumers(),
                            consumerDefinitionClass,
                            ignoredUsers.consumers(),
                            resolveUserDefinitionClass(ignoredUsers.consumerClass()),
                            focusType
                    );

            return new TestConfiguration(
                    producerIdentifiers,
                    consumerIdentifier,
                    focusType,
                    runner,
                    defaultException
            );
        } else {
            return new TestConfiguration(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    FocusType.NONE,
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
     * @param testUsers RunWithUsers annotation
     * @param multiUserTestConfig Optional configurations class level configuration
     * @param defaultRunner Runner class to use by default
     * @return Test configuration
     */
    public static TestConfiguration fromRunWithUsers(RunWithUsers testUsers, MultiUserTestConfig multiUserTestConfig, Class<?> defaultRunner) {
        final Class<? extends Throwable> defaultException = Defaults.getDefaultException();

        final Collection<UserIdentifier> producerIdentifiers = Optional.ofNullable(testUsers).map(r -> getDefinitions(
                r.producers(),
                resolveUserDefinitionClass(r.producerClass())
                        .getUsers(),
                DEFAULT_MERGE_STRATEGY,
                resolveFocusType(r)
        )).orElseGet(Collections::emptySet);
        final Collection<UserIdentifier> consumerIdentifier = Optional.ofNullable(testUsers).map(r -> getDefinitions(
                r.consumers(),
                resolveUserDefinitionClass(r.consumerClass())
                        .getUsers(),
                DEFAULT_MERGE_STRATEGY,
                resolveFocusType(r)
        )).orElseGet(Collections::emptySet);
        final FocusType focusMode = Optional.ofNullable(testUsers)
                .map(TestConfiguration::resolveFocusType)
                .orElse(FocusType.NONE);
        final Class<?> runner = Optional.ofNullable(multiUserTestConfig)
                .map(MultiUserTestConfig::runner)
                .orElse(defaultRunner);

        return new TestConfiguration(
                producerIdentifiers,
                consumerIdentifier,
                focusMode,
                runner,
                defaultException
        );
    }

    private static FocusType resolveFocusType(RunWithUsers runWithUsers) {
        return Optional.ofNullable(runWithUsers)
                .map(r -> runWithUsers.focusEnabled() ? FocusType.FOCUS : FocusType.NONE)
                .orElse(FocusType.NONE);
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
        this.runner = runner;
        this.defaultException = defaultException;
        this.focusType = focusType;
    }

    public Collection<UserIdentifier> getProducerIdentifiers() {
        return producerIdentifiers;
    }

    public Collection<UserIdentifier> getConsumerIdentifiers() {
        return consumerIdentifiers;
    }

    public Optional<Class<?>> getRunner() {
        return Optional.ofNullable(runner);
    }

    public Optional<Class<? extends Throwable>> getDefaultException() {
        return Optional.ofNullable(defaultException);
    }

    public FocusType getFocusType() {
        return focusType;
    }
}
