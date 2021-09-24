package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import org.junit.runners.model.FrameworkMethod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for filtering methods that should be run with the initialized
 * producer and consumer identifiers.
 */
public class TestMethodFilter {
    private final UserIdentifier producerIdentifier;
    private final UserIdentifier consumerIdentifier;

    /**
     * Initialize filter with the given identifiers which are used for filtering.
     * @param producerIdentifier Producer identifier
     * @param consumerIdentifier Consumer identifier
     */
    public TestMethodFilter(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        Objects.requireNonNull(producerIdentifier);
        Objects.requireNonNull(consumerIdentifier);

        this.producerIdentifier = producerIdentifier;
        this.consumerIdentifier = consumerIdentifier;
    }

    /**
     * @param frameworkMethod Method to test against identifiers
     * @return True if given method should be run, otherwise false.
     */
    public boolean shouldRun(FrameworkMethod frameworkMethod) {
        Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(frameworkMethod.getAnnotation(RunWithUsers.class));
        Optional<IgnoreForUsers> ignoreForUsersAnnotation =
                Optional.ofNullable(frameworkMethod.getAnnotation(IgnoreForUsers.class));
        final Class<?> declaringClass = frameworkMethod.getDeclaringClass();

        return shouldRun(runWithUsersAnnotation, ignoreForUsersAnnotation, declaringClass, BlockMultiUserTestClassRunner.class);
    }

    public boolean shouldRun(Optional<RunWithUsers> runWithUsersAnnotation, Optional<IgnoreForUsers> ignoreForUsersAnnotation, Class<?> declaringClass, Class<?> runner) {
        if (runWithUsersAnnotation.isPresent() && ignoreForUsersAnnotation.isPresent()) {
            throw new IllegalStateException("Method can only have RunWithUsers or IgnoreForUsers annotation but not both.");
        }

        TestConfiguration configuration;
        if (runWithUsersAnnotation.isPresent()) {
            configuration = TestConfiguration.fromRunWithUsers(
                    runWithUsersAnnotation,
                    Optional.empty(),
                    runner
            );
        } else if (ignoreForUsersAnnotation.isPresent()) {

            configuration = TestConfiguration.fromIgnoreForUsers(
                    ignoreForUsersAnnotation,
                    Optional.ofNullable(declaringClass.getAnnotation(RunWithUsers.class)),
                    runner
            );
        } else {
            // FIXME: Is this correct?
            return true;
        }

        Collection<UserIdentifier> filterProducers = configuration.getProducerIdentifiers();
        Collection<UserIdentifier> filterConsumers = configuration.getConsumerIdentifiers();

        boolean shouldRun = true;
        if (!filterProducers.isEmpty()) {
            shouldRun = filterProducers.contains(producerIdentifier);
        }
        if (!filterConsumers.isEmpty()) {
            boolean consumerWithProducerRole = filterConsumers.contains(UserIdentifier.getWithProducerRole(FocusType.NONE))
                    || consumerIdentifier.equals(UserIdentifier.getWithProducerRole(FocusType.NONE));
            boolean filterProducerContainsCurrentConsumer = filterProducers.contains(consumerIdentifier);

            shouldRun = shouldRun && (filterConsumers.contains(consumerIdentifier)
                    || (consumerWithProducerRole && (filterProducerContainsCurrentConsumer)));
        }

        return shouldRun;
    }

    /**
     * @param methods List of methods to filter
     * @return List of methods that should be run with the initialized identifiers.
     */
    public List<FrameworkMethod> filter(List<FrameworkMethod> methods) {
        return methods.stream()
                .filter(this::shouldRun)
                .collect(Collectors.toList());
    }
}
