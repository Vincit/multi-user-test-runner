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

/**
 * Helper class for delegating calls from JUnit runner.
 * Does some required method filtering and helps executing
 * Before methods in correct order.
 */
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
        } else if (hasComponentConfig(testInstance)){
            return testInstance;
        } else {
            throw new IllegalStateException("Invalid test configuration. " +
                    "Test has to extend UserRoleIT or have configuration annotated with @MultiUserConfigClass.");
        }

    }

    private boolean hasComponentConfig(Object testInstance) {
        try {
            return findFieldWithConfig(testInstance).isPresent();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private MultiUserConfig getConfigComponent(Object testInstance) {
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

    private Optional<Field> findFieldWithConfig(Object testInstance) throws IllegalAccessException {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(MultiUserConfigClass.class)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public Statement withBefores(final TestClass testClass, final Object target, final Statement statement) {
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(
                Before.class);
        final Statement runLoginBeforeTestMethod = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                preEvaluateConfig();
                statement.evaluate();
            }

            public void preEvaluateConfig() {
                UserRoleIT userRoleIt = null;
                if (target instanceof UserRoleIT) {
                    userRoleIt = (UserRoleIT) target;
                } else {
                    userRoleIt = getConfigComponent(target);
                    userRoleIt.setUsers(producerIdentifier, userIdentifier);
                }

                userRoleIt.logInAs(LoginRole.PRODUCER);
            }
        };

        if (target instanceof UserRoleIT) {
            return befores.isEmpty() ? runLoginBeforeTestMethod : new RunBefores(
                    runLoginBeforeTestMethod, befores, target
            );
        } else {
            return befores.isEmpty() ? runLoginBeforeTestMethod : new RunBefores(
                    runLoginBeforeTestMethod, befores, target
            );
        }


    }
}
