package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.test.UserRoleIT;
import org.junit.Before;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class RunnerDelegate {

    private final UserIdentifier producerIdentifier;
    private final UserIdentifier userIdentifier;
    private final TestMethodFilter shouldRunChecker;

    public RunnerDelegate(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        Objects.requireNonNull(producerIdentifier);
        Objects.requireNonNull(consumerIdentifier);

        this.producerIdentifier = producerIdentifier;
        this.userIdentifier = consumerIdentifier;
        this.shouldRunChecker = new TestMethodFilter(producerIdentifier, consumerIdentifier);
    }

    // Only for testing
    RunnerDelegate(UserIdentifier producerIdentifier, UserIdentifier userIdentifier, TestMethodFilter shouldRunChecker) {
        Objects.requireNonNull(producerIdentifier);
        Objects.requireNonNull(userIdentifier);
        Objects.requireNonNull(shouldRunChecker);

        this.producerIdentifier = producerIdentifier;
        this.userIdentifier = userIdentifier;
        this.shouldRunChecker = shouldRunChecker;
    }

    public List<FrameworkMethod> filterMethods(List<FrameworkMethod> methods) {
        List<FrameworkMethod> filteredMethods = shouldRunChecker.filter(methods);
        if (!filteredMethods.isEmpty()) {
            return filteredMethods;
        } else {
            return methods;
        }
    }

    public boolean isIgnored(FrameworkMethod child, boolean isIgnoredByParent) {
        return !shouldRunChecker.shouldRun(child) || isIgnoredByParent;
    }

    public String testName(FrameworkMethod method) {
        return String.format("%s", method.getName());
    }

    public String getName(TestClass testClass) {
        return String.format("%s", getIdentifierDescription());
    }

    private String getIdentifierDescription() {
        return String.format("producer={%s}, consumer={%s}", producerIdentifier, userIdentifier);
    }

    public Object createTest(Object testInstance) {

        if (testInstance instanceof UserRoleIT) {
            UserRoleIT roleItInstance = (UserRoleIT) testInstance;
            roleItInstance.setUsers(producerIdentifier, userIdentifier);
            return roleItInstance;
        } else if (hasConfigComponent(testInstance)) {
            UserRoleIT roleItInstance = getConfigComponent(testInstance).get();
            roleItInstance.setUsers(producerIdentifier, userIdentifier);
            return testInstance;
        } else {
            throw new IllegalStateException("Test class must be of type " + UserRoleIT.class.getSimpleName());
        }

    }

    private Optional<MultiUserConfig> getConfigComponent(Object testInstance) {
        try {
            Optional<MultiUserConfig> config = Optional.empty();
            for (Field field : testInstance.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(MultiUserConfigClass.class) && !config.isPresent()) {
                    field.setAccessible(true);
                    config = Optional.of((MultiUserConfig) field.get(testInstance));
                    field.setAccessible(false);
                }
            }

            if (config.isPresent()) {
                return config;
            } else {
                throw new IllegalStateException("MultiUserConfigClass not found on " + testInstance.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasConfigComponent(Object testInstance) {
        return getConfigComponent(testInstance).isPresent();
    }

    public Statement withBefores(TestClass testClass, final Object target, final Statement statement) {
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(
                Before.class);
        if (target instanceof MultiUserConfig) {
            return statement;
        } else {
            final Statement runLoginBeforeTestMethod = new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    if (target instanceof UserRoleIT) {
                        ((UserRoleIT)target).logInAs(LoginRole.PRODUCER);
                    }
                    statement.evaluate();
                }
            };
            return befores.isEmpty() ? runLoginBeforeTestMethod : new RunBefores(runLoginBeforeTestMethod,
                    befores, target);
        }


    }
}
