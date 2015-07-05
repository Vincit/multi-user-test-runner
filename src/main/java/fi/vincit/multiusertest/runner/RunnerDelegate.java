package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.TestMethodFilter;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.util.List;

public class RunnerDelegate {

    private UserIdentifier creatorIdentifier;
    private UserIdentifier userIdentifier;
    private TestMethodFilter shouldRunChecker;

    public RunnerDelegate(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        assert userIdentifier != null;
        assert creatorIdentifier != null;
        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
        this.shouldRunChecker = new TestMethodFilter(creatorIdentifier, userIdentifier);
    }

    // Only for testing
    RunnerDelegate(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier, TestMethodFilter shouldRunChecker) {
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

    protected String testName(FrameworkMethod method) {
        return String.format("%s: %s", method.getName(), getIdentifierDescription());
    }

    protected String getName(TestClass testClass) {
        return String.format("%s: %s", testClass.getName(), getIdentifierDescription());
    }

    private String getIdentifierDescription() {
        return String.format("creator = %s; user = %s", creatorIdentifier, userIdentifier);
    }

    public AbstractUserRoleIT createTest(Object testInstance) {
        if (testInstance instanceof AbstractUserRoleIT) {
            AbstractUserRoleIT roleItInstance = (AbstractUserRoleIT) testInstance;
            roleItInstance.setUsers(creatorIdentifier, userIdentifier);
            return roleItInstance;
        } else {
            throw new IllegalStateException("Test class must be of type " + AbstractUserRoleIT.class.getSimpleName());
        }
    }
}
