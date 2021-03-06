package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.TestMethodFilter;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        final List<TestTemplateInvocationContext> contextList = new ArrayList<>();
        for (UserIdentifier producerIdentifier : configuration.getProducerIdentifiers()) {
            for (UserIdentifier consumerIdentifier : configuration.getConsumerIdentifiers()) {
                createContext(context, producerIdentifier, consumerIdentifier)
                        .ifPresent(contextList::add);
            }
        }
        return contextList;
    }

    private Optional<TemplateInvocationContext> createContext(ExtensionContext context, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        final Method testMethod = context.getRequiredTestMethod();
        final Class<?> declaringClass = context.getRequiredTestClass();

        final Optional<RunWithUsers> runWithUsersAnnotation =
        Optional.ofNullable(testMethod.getAnnotation(RunWithUsers.class));
        final Optional<IgnoreForUsers> ignoreForUsersAnnotation =
                Optional.ofNullable(testMethod.getAnnotation(IgnoreForUsers.class));

        final TestMethodFilter testMethodFilter = new TestMethodFilter(producerIdentifier, consumerIdentifier);

        if (testMethodFilter.shouldRun(runWithUsersAnnotation, ignoreForUsersAnnotation, declaringClass, Object.class)) {
             return Optional.of(new TemplateInvocationContext(producerIdentifier, consumerIdentifier));
        } else {
            return Optional.empty();
        }
    }

    private TestConfiguration getConfigurationOrThrow(Class<?> testClass) {
        final Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(testClass.getAnnotation(RunWithUsers.class));
        final Optional<MultiUserTestConfig> config =
                Optional.ofNullable(testClass.getAnnotation(MultiUserTestConfig.class));

        if (runWithUsersAnnotation.isPresent()) {
            return TestConfiguration.fromRunWithUsers(runWithUsersAnnotation, config, Object.class);
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + testClass.getName()
                            + " Use " + RunWithUsers.class.getName() + " class"
            );
        }
    }

}
