package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.model.InitializationError;

import java.util.Set;

public class MockTestRunner extends BlockMultiUserTestClassRunner {

    public MockTestRunner(Class<?> clazz, Set<UserIdentifier> allowedIdentifiers, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier, FocusType focusType) throws InitializationError {
        super(clazz, allowedIdentifiers, producerIdentifier, consumerIdentifier, focusType);
    }
}
