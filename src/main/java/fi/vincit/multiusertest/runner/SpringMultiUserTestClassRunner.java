package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.CheckShouldRun;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Spring specific runner. Uses SpringJUnit4ClassRunner to initialize
 * spring context.
 */
public class SpringMultiUserTestClassRunner extends SpringJUnit4ClassRunner {

    private UserIdentifier creatorIdentifier;
    private UserIdentifier userIdentifier;
    private CheckShouldRun shouldRunChecker;


    public SpringMultiUserTestClassRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) throws InitializationError {
        super(clazz);
        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
        this.shouldRunChecker = new CheckShouldRun(creatorIdentifier, userIdentifier);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        return !shouldRunChecker.shouldRun(child) || super.isIgnored(child);
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> methods = shouldRunChecker.getMethodsToRun(super.getChildren());
        if (!methods.isEmpty()) {
            return methods;
        } else {
            return super.getChildren();
        }
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return String.format("%s: %s", method.getName(), getIdentifierDescription());
    }

    @Override
    protected String getName() {
        return String.format("%s: %s", getTestClass().getName(), getIdentifierDescription());
    }

    private String getIdentifierDescription() {
        return String.format("creator = %s; user = %s", creatorIdentifier, userIdentifier);
    }

    @Override
    protected Object createTest() throws Exception {
        Object testInstance = super.createTest();
        if (testInstance instanceof AbstractUserRoleIT) {
            AbstractUserRoleIT roleItInstance = (AbstractUserRoleIT) testInstance;
            roleItInstance.setUsers(creatorIdentifier, userIdentifier);
            return roleItInstance;
        } else {
            throw new IllegalStateException("Test class must be of type " + AbstractUserRoleIT.class.getSimpleName());
        }
    }

}
