package fi.vincit.multiusertest.util;

import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import fi.vincit.multiusertest.test.UserRoleIT;

public class RunnerDelegate {

    private final UserIdentifier creatorIdentifier;
    private final UserIdentifier userIdentifier;
    private final TestMethodFilter shouldRunChecker;

    public RunnerDelegate(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        Objects.requireNonNull(creatorIdentifier);
        Objects.requireNonNull(userIdentifier);

        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
        this.shouldRunChecker = new TestMethodFilter(creatorIdentifier, userIdentifier);
    }

    // Only for testing
    RunnerDelegate(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier, TestMethodFilter shouldRunChecker) {
        Objects.requireNonNull(creatorIdentifier);
        Objects.requireNonNull(userIdentifier);
        Objects.requireNonNull(shouldRunChecker);

        this.creatorIdentifier = creatorIdentifier;
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
        return String.format("%s: %s", method.getName(), getIdentifierDescription());
    }

    public String getName(TestClass testClass) {
        return String.format("%s: %s", testClass.getName(), getIdentifierDescription());
    }

    private String getIdentifierDescription() {
        return String.format("creator = %s; user = %s", creatorIdentifier, userIdentifier);
    }

    public UserRoleIT createTest(Object testInstance) {
        if (testInstance instanceof UserRoleIT) {
            UserRoleIT roleItInstance = (UserRoleIT) testInstance;
            roleItInstance.setUsers(creatorIdentifier, userIdentifier);
            return roleItInstance;
        } else {
            throw new IllegalStateException("Test class must be of type " + UserRoleIT.class.getSimpleName());
        }
    }

    public Statement withBefores(TestClass testClass, final Object target, final Statement statement) {
        List<FrameworkMethod> befores = testClass.getAnnotatedMethods(
                Before.class);
        Statement runLoginBeforeTestMethod = new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (target instanceof UserRoleIT) {
                    ((UserRoleIT)target).logInAs(LoginRole.CREATOR);
                }
                statement.evaluate();
            }
        };
        return befores.isEmpty() ? runLoginBeforeTestMethod : new RunBefores(runLoginBeforeTestMethod,
                befores, target);
    }
}
