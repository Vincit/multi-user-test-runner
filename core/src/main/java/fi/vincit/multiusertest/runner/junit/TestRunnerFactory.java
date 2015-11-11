package fi.vincit.multiusertest.runner.junit;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.TestClass;

import fi.vincit.multiusertest.util.UserIdentifier;

public class TestRunnerFactory {

    private final TestClass testClass;
    private final Constructor runnerConstructor;

    public TestRunnerFactory(TestClass testClass, Constructor runnerConstructor) {
        this.testClass = testClass;
        this.runnerConstructor = runnerConstructor;
    }

    public List<Runner> createRunnersForRoles(Collection<UserIdentifier> creatorIdentifiers, Collection<UserIdentifier> userIdentifiers) throws Exception {
        List<Runner> runners = new ArrayList<>();
        if (userIdentifiers.isEmpty()) {
            userIdentifiers.add(UserIdentifier.getNewUser());
        }
        validateCreators(creatorIdentifiers);
        validateUsers(creatorIdentifiers, userIdentifiers);

        for (UserIdentifier creatorIdentifier : creatorIdentifiers) {
            for (UserIdentifier userIdentifier : userIdentifiers) {
                Object parentRunner = runnerConstructor.newInstance(
                        testClass.getJavaClass(),
                        creatorIdentifier,
                        userIdentifier
                );
                runners.add((ParentRunner) parentRunner);

            }
        }
        return runners;
    }

    void validateUsers(Collection<UserIdentifier> creatorIdentifiers, Collection<UserIdentifier> userIdentifiers) {
        boolean containsExistingUserDefinition = false;
        for (UserIdentifier identifier : creatorIdentifiers) {
            if (identifier.getType() == UserIdentifier.Type.USER) {
                containsExistingUserDefinition = true;
            }
        }

        if (containsExistingUserDefinition
                && userIdentifiers.contains(UserIdentifier.getNewUser())) {
            throw new IllegalArgumentException("User definitions can't contain NEW_USER when creators have a 'user' definition");
        }
    }

    void validateCreators(Collection<UserIdentifier> creatorIdentifiers) {
        if (creatorIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("Creator must be specified");
        }

        if (creatorIdentifiers.contains(UserIdentifier.getCreator())) {
            throw new IllegalArgumentException("Creator can't use PRODUCER role");
        }

        if (creatorIdentifiers.contains(UserIdentifier.getNewUser())) {
            throw new IllegalArgumentException("Creator can't use NEW_USER role");
        }
    }

}