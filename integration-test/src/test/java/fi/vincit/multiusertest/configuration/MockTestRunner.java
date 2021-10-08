package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.runner.junit.RunnerConfig;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import org.junit.runners.model.InitializationError;

public class MockTestRunner extends BlockMultiUserTestClassRunner {

    public MockTestRunner(RunnerConfig runnerConfig) throws InitializationError {
        super(runnerConfig);
    }
}
