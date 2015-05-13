package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Runner based on BlockJUnit4ClassRunner. Works with plain Java code.
 */
public class BlockMultiUserTestClassRunner extends BlockJUnit4ClassRunner {

    private UserIdentifier creatorIdentifier;
    private UserIdentifier userIdentifier;


    public BlockMultiUserTestClassRunner(Class<?> clazz, UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) throws InitializationError {
        super(clazz);
        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
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
