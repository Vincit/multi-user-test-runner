package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.runner.junit.RunnerConfig;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.TestMethodFilter;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

public class JUnit5MultiUserTestRunner implements
        TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        final TestConfiguration configuration =
                getConfigurationOrThrow(context.getRequiredTestClass());

        return generateTestInvocations(context, configuration).stream();
    }

    private List<TestTemplateInvocationContext> generateTestInvocations(ExtensionContext context, TestConfiguration configuration) {
        final Set<UserIdentifier> allowedIdentifiers = new HashSet<>();
        allowedIdentifiers.addAll(configuration.getConsumerIdentifiers());
        allowedIdentifiers.addAll(configuration.getProducerIdentifiers());

        final List<TestTemplateInvocationContext> contextList = new ArrayList<>();
        for (UserIdentifier producerIdentifier : configuration.getProducerIdentifiers()) {
            for (UserIdentifier consumerIdentifier : configuration.getConsumerIdentifiers()) {
                RunnerConfig runnerConfig = new RunnerConfig(
                        context.getRequiredTestClass(),
                        allowedIdentifiers,
                        producerIdentifier,
                        consumerIdentifier,
                        configuration.getFocusType()
                );

                createContext(context, runnerConfig, producerIdentifier, consumerIdentifier)
                        .ifPresent(contextList::add);
            }
        }
        return contextList;
    }

    private Optional<TemplateInvocationContext> createContext(ExtensionContext context, RunnerConfig configuration, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        final Method testMethod = context.getRequiredTestMethod();
        final Class<?> declaringClass = context.getRequiredTestClass();

        final TestMethodFilter testMethodFilter = new TestMethodFilter(producerIdentifier, consumerIdentifier);

        final boolean shouldRun = testMethodFilter.shouldRun(
                testMethod.getAnnotation(RunWithUsers.class),
                testMethod.getAnnotation(IgnoreForUsers.class),
                declaringClass,
                Object.class
        );

        if (shouldRun) {
             return Optional.of(new TemplateInvocationContext(configuration, producerIdentifier, consumerIdentifier));
        } else {
            return Optional.empty();
        }
    }

    private TestConfiguration getConfigurationOrThrow(Class<?> testClass) {
        final Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(testClass.getAnnotation(RunWithUsers.class));

        if (runWithUsersAnnotation.isPresent()) {
            return TestConfiguration.fromRunWithUsers(
                    runWithUsersAnnotation.get(),
                    testClass.getAnnotation(MultiUserTestConfig.class),
                    Object.class
            );
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + testClass.getName()
                            + " Use " + RunWithUsers.class.getName() + " class"
            );
        }
    }

}
