package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.TestMethodFilter;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static fi.vincit.multiusertest.util.TestNameUtil.getIdentifiers;

public class JUnit5MultiUserTestRunner implements
        TestTemplateInvocationContextProvider
{

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        final Optional<Class<?>> testClass = context.getTestClass();

        TestConfiguration configuration = getConfigurationOrThrow(testClass);

        List<TestTemplateInvocationContext> contextList = new ArrayList<>();
        for (UserIdentifier producerIdentifier : configuration.getProducerIdentifiers()) {
            for (UserIdentifier consumerIdentifier : configuration.getConsumerIdentifiers()) {
                final TestMethodFilter testMethodFilter = new TestMethodFilter(producerIdentifier, consumerIdentifier);

                final Method testMethod = context.getRequiredTestMethod();
                final Class<?> declaringClass = context.getRequiredTestClass();

                Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(testMethod.getAnnotation(RunWithUsers.class));
                Optional<IgnoreForUsers> ignoreForUsersAnnotation =
                        Optional.ofNullable(testMethod.getAnnotation(IgnoreForUsers.class));


                if (testMethodFilter.shouldRun(runWithUsersAnnotation, ignoreForUsersAnnotation, declaringClass, Object.class)) {
                    contextList.add(invocationContext(producerIdentifier, consumerIdentifier));
                }
            }
        }

        return contextList.stream();
    }

    private TestConfiguration getConfigurationOrThrow(Optional<Class<?>> testClass) {
        Class<?> jc = testClass.get();
        Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(jc.getAnnotation(RunWithUsers.class));
        Optional<MultiUserTestConfig> config =
                Optional.ofNullable(jc.getAnnotation(MultiUserTestConfig.class));

        if (runWithUsersAnnotation.isPresent()) {
            return TestConfiguration.fromRunWithUsers(runWithUsersAnnotation, config, Object.class);
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + jc.getName()
                            + " Use " + RunWithUsers.class.getName() + " class"
            );
        }
    }

    private static MultiUserConfig getConfigComponent(Object testInstance) {
        Optional<MultiUserConfig> config = Optional.empty();
        try {
            Optional<Field> field = findFieldWithConfig(testInstance);

            if (field.isPresent()) {
                Field fieldInstance = field.get();
                fieldInstance.setAccessible(true);
                config = Optional.ofNullable((MultiUserConfig) fieldInstance.get(testInstance));
                fieldInstance.setAccessible(false);
            }

            if (config.isPresent()) {
                return config.get();
            } else {
                throw new IllegalStateException("MultiUserConfigClass not found on " + testInstance.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Optional<Field> findFieldWithConfig(Object testInstance) throws IllegalAccessException {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(MultiUserConfigClass.class)) {
                return Optional.of(field);
            }
        }

        for (Field field : testInstance.getClass().getFields()) {
            if (field.isAnnotationPresent(MultiUserConfigClass.class)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    private TestTemplateInvocationContext invocationContext(UserIdentifier producer, UserIdentifier consumer) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return getIdentifiers(producer, consumer);
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return Collections.singletonList(new ParameterResolver() {
                    @Override
                    public boolean supportsParameter(ParameterContext parameterContext,
                            ExtensionContext extensionContext) {
                        return parameterContext.getParameter().getType().equals(Authorization.class);
                    }

                    @Override
                    public Object resolveParameter(ParameterContext parameterContext,
                            ExtensionContext extensionContext) {
                        final JUnit5Authorization authorization = new JUnit5Authorization();
                        final MultiUserConfig userIt = getConfigComponent(extensionContext.getTestInstance().get());
                        userIt.setUsers(producer, consumer);
                        userIt.setAuthorizationRule(authorization, null);
                        userIt.initialize();

                        authorization.setRole(producer);

                        userIt.logInAs(LoginRole.PRODUCER);

                        return authorization;
                    }
                });
            }
        };
    }
}
