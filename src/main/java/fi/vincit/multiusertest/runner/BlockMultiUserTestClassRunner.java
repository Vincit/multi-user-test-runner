package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.List;

/**
 * Runner based on BlockJUnit4ClassRunner. Works with plain Java code.
 */
public class BlockMultiUserTestClassRunner extends BlockJUnit4ClassRunner {

    private RunnerDelegate runnerDelegate;

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

}
