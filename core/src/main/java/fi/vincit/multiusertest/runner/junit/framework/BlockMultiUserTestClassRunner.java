package fi.vincit.multiusertest.runner.junit.framework;

import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import fi.vincit.multiusertest.util.RunnerDelegate;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * Runner based on BlockJUnit4ClassRunner. Works with plain Java code.
 */
public class BlockMultiUserTestClassRunner extends BlockJUnit4ClassRunner {

    private final RunnerDelegate runnerDelegate;

    public BlockMultiUserTestClassRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) throws InitializationError {
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

    @Override
    protected Statement withBefores(FrameworkMethod method, final Object target, Statement statement) {
        return runnerDelegate.withBefores(
                getTestClass(),
                target,
                statement
        );
    }


}
