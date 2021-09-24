package fi.vincit.multiusertest.runner.junit.framework;

import fi.vincit.multiusertest.runner.junit.RunnerDelegate;
import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.Set;

/**
 * Runner based on BlockJUnit4ClassRunner. Works with plain Java code.
 */
public class BlockMultiUserTestClassRunner extends BlockJUnit4ClassRunner {

    private final RunnerDelegate runnerDelegate;

    public BlockMultiUserTestClassRunner(Class<?> clazz, Set<UserIdentifier> allowedIdentifiers, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier, FocusType focusType) throws InitializationError {
        super(clazz);
        this.runnerDelegate = new RunnerDelegate(allowedIdentifiers, producerIdentifier, consumerIdentifier, focusType);
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
    protected Statement withBefores(FrameworkMethod method, final Object target, Statement statement) {
        return runnerDelegate.withBefores(
                getTestClass(),
                target,
                statement
        );
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        return childrenInvoker(notifier);
    }
}
