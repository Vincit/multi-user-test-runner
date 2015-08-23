package fi.vincit.multiusertest.runner.junit;

import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.Suite;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * Test runner for executing tests with multiple creator-user combinations. Creator
 * is the user creating a resource e.g. project or a customer. User is the user who
 * later uses/edits/deletes the previously created resource.
 *
 * MultiUserTestRunner requires the test class to have {@link fi.vincit.multiusertest.annotation.TestUsers} annotation which
 * will define with what users the tests are executed. This annotation also allows the test runner to be changed.
 *
 * There are two different type of entities that can be used: roles and users. Roles
 * create a new user to DB with the given role. Users use existing users that are found in
 * the DB. The syntax is [role|user]:[role name|username] e.g. "role:ROLE_ADMIN" or "user:username".
 *
 * There are also two special entities that can be used for test user: {@link fi.vincit.multiusertest.annotation.TestUsers#CREATOR} and
 * {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER}. CREATOR uses the the same user as the resource was generated. NEW_USER
 * creates a new user with the same role as the creator had.
 *
 * If no users are defined {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER} will be used as the default
 * user entity. Creators can't use {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER} or
 * {@link fi.vincit.multiusertest.annotation.TestUsers#CREATOR} roles since those roles are tied to the current
 * creator role.
 *
 */
public class MultiUserTestRunner extends Suite {

    private static final List<Runner> NO_RUNNERS = Collections
            .<Runner>emptyList();
    public static final String ROLE_PREFIX = "role:";
    public static final String USER_PREFIX = "user:";

    private final List<Runner> runners;

    public MultiUserTestRunner(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        TestConfiguration configuration = getConfigurationOrThrow();
        TestRunnerFactory runnerFactory = createTestRunner(configuration);
        this.runners = runnerFactory.createRunnersForRoles(
                configuration.getCreatorIdentifiers(),
                configuration.getUserIdentifiers()
        );
    }

    private TestRunnerFactory createTestRunner(TestConfiguration testConfiguration) throws NoSuchMethodException {
        if (testConfiguration.getRunner().isPresent()) {
            try {
                return new TestRunnerFactory(
                        getTestClass(),
                        testConfiguration.getRunner().get().getConstructor(Class.class, UserIdentifier.class, UserIdentifier.class)
                );
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodException("Runner must have constructor with class, UserIdentifier, UserIdentifier parameters");
            }

        } else {
            throw new IllegalArgumentException("TestUsers.runner must not be null");
        }
    }


    private TestConfiguration getConfigurationOrThrow() throws Exception {
        Optional<TestUsers> testRolesAnnotation =
                Optional.ofNullable(getTestClass().getJavaClass().getAnnotation(TestUsers.class));
        Optional<MultiUserTestConfig> config =
                Optional.ofNullable(getTestClass().getJavaClass().getAnnotation(MultiUserTestConfig.class));
        if (testRolesAnnotation.isPresent()) {
            return TestConfiguration.fromTestUsers(testRolesAnnotation, config);
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + getTestClass().getName()
                            + " Use " + TestUsers.class.getName() + " class"
            );
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

}
