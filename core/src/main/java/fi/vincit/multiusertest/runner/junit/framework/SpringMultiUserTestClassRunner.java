package fi.vincit.multiusertest.runner.junit.framework;

import java.util.List;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.vincit.multiusertest.util.RunnerDelegate;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * Spring specific runner. Uses SpringJUnit4ClassRunner to initialize
 * spring context.
 */
public class SpringMultiUserTestClassRunner extends SpringJUnit4ClassRunner {

    private RunnerDelegate runnerDelegate;

    public SpringMultiUserTestClassRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) throws InitializationError {
        super(clazz);
        this.runnerDelegate = new RunnerDelegate(creatorIdentifier, userIdentifier);
    }

    @Override
    protected boolean isIgnored(FrameworkMethod child) {
        return runnerDelegate.isIgnored(child, super.isIgnored(child));
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        return runnerDelegate.filterMethods(super.getChildren());
    }

    @Override
    protected String testName(FrameworkMethod method) {
        return runnerDelegate.testName(method);
    }

    @Override
    protected String getName() {
        return runnerDelegate.getName(getTestClass());
    }

    @Override
    protected Object createTest() throws Exception {
        return runnerDelegate.createTest(super.createTest());
    }

}
