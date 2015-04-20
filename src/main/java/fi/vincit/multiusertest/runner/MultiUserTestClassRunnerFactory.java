package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public interface MultiUserTestClassRunnerFactory {
    ParentRunner<FrameworkMethod> createTestRunner(Class testClass, UserIdentifier creator, UserIdentifier user) throws InitializationError;
}
