package fi.vincit.multiusertest.runner.junit;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runner.Runner;
import org.junit.runners.Suite;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * Test runner for executing tests with multiple producer-consumer combinations. Producer
 * is the user creating a resource e.g. project or a customer. Consumer is the user who
 * later uses/edits/deletes the previously created resource. Used with JUnit's
 * {@link org.junit.runner.RunWith} annotation.
 * </p>
 * <p>
 * MultiUserTestRunner requires the test class to have {@link fi.vincit.multiusertest.annotation.RunWithUsers} annotation which
 * will define with what users the tests are executed. To configure the test runner use
 * {@link fi.vincit.multiusertest.annotation.MultiUserTestConfig} annotation.
 * </p>
 * <p>
 * There are two different type of definitions that can be used: roles and users. Roles
 * create a new user to DB with the given role. Users use existing users that are found in
 * the DB. The syntax is <i>[role|user]:[role name|username]</i> e.g. <i>"role:ROLE_ADMIN"</i> or <i>"user:username"</i>.
 * </p>
 * <p>
 * There are also two special definitions that can be used for test consumers (not producers): {@link fi.vincit.multiusertest.annotation.RunWithUsers#PRODUCER} and
 * {@link fi.vincit.multiusertest.annotation.RunWithUsers#WITH_PRODUCER_ROLE}. {@link fi.vincit.multiusertest.annotation.RunWithUsers#PRODUCER} uses
 * the the same user as the resource was generated. {@link fi.vincit.multiusertest.annotation.RunWithUsers#WITH_PRODUCER_ROLE}
 * creates a new user with the same role as the producer had.
 * </p>
 * <p>
 * There is also a special role {@link fi.vincit.multiusertest.annotation.RunWithUsers#ANONYMOUS} both for
 * the producer and the consumer. This means that the current consumer or producer isn't logged in.
 * </p>
 * <p>
 * If no users are defined {@link fi.vincit.multiusertest.annotation.RunWithUsers#WITH_PRODUCER_ROLE} will be used as the default
 * consumer definition. Producers can't use {@link fi.vincit.multiusertest.annotation.RunWithUsers#WITH_PRODUCER_ROLE} or
 * {@link fi.vincit.multiusertest.annotation.RunWithUsers#PRODUCER} roles since those roles are tied to the current
 * producer role.
 * </p>
 */
public class MultiUserTestRunner extends Suite {

    private static final List<Runner> NO_RUNNERS = Collections
            .emptyList();
    public static final String ROLE_PREFIX = "role:";
    public static final String USER_PREFIX = "user:";

    private final List<Runner> runners;

    public MultiUserTestRunner(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        TestConfiguration configuration = getConfigurationOrThrow();
        TestRunnerFactory runnerFactory = createTestRunner(configuration);
        this.runners = runnerFactory.createRunnersForRoles(
                configuration.getProducerIdentifiers(),
                configuration.getConsumerIdentifiers()
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
        Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(getTestClass().getJavaClass().getAnnotation(RunWithUsers.class));
        Optional<MultiUserTestConfig> config =
                Optional.ofNullable(getTestClass().getJavaClass().getAnnotation(MultiUserTestConfig.class));

        if (runWithUsersAnnotation.isPresent()) {
            return TestConfiguration.fromRunWithUsers(runWithUsersAnnotation, config);
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + getTestClass().getName()
                            + " Use " + RunWithUsers.class.getName() + " class"
            );
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

}
