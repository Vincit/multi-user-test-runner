package fi.vincit.multiusertest.runner.junit.framework;

import fi.vincit.multiusertest.util.RunnerDelegate;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks;

import java.util.List;

/**
 * Spring specific runner. Uses SpringJUnit4ClassRunner to initialize
 * spring context.
 */
public class SpringMultiUserTestClassRunner extends SpringJUnit4ClassRunner {

    private final RunnerDelegate runnerDelegate;

    public SpringMultiUserTestClassRunner(Class<?> clazz, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) throws InitializationError {
        super(clazz);
        this.runnerDelegate = new RunnerDelegate(producerIdentifier, consumerIdentifier);
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
        return runnerDelegate.validateTestInstance(super.createTest());
    }

    @Override
    protected Statement withBefores(FrameworkMethod frameworkMethod, final Object testInstance, Statement statement) {
        Statement junitBefores = runnerDelegate.withBefores(
                getTestClass(),
                testInstance,
                statement
        );
        return new RunBeforeTestMethodCallbacks(
                junitBefores, testInstance, frameworkMethod.getMethod(), getTestContextManager()
        );
    }
}
