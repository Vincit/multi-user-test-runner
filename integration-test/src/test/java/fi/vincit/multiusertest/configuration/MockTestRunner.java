package fi.vincit.multiusertest.configuration;

import org.junit.runners.model.InitializationError;

import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.UserIdentifier;

public class MockTestRunner extends BlockMultiUserTestClassRunner {

    public MockTestRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) throws InitializationError {
        super(clazz, creatorIdentifier, userIdentifier);
    }
}
