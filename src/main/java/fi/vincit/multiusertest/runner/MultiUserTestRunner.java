package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test runner for executing tests with multiple creator-user combinations. Creator
 * is the user creating a resource e.g. project or a customer. User is the user who
 * later uses/edits/deletes the previously created resource.
 *
 * MultiUserTestRunner requires the test class to have {@link fi.vincit.multiusertest.annotation.TestUsers} annotation which
 * will define with what users the tests are executed.
 *
 * There are two different type of entities that can be used: roles and users. Roles
 * create a new user to DB with the given role. Users use existing users that are found in
 * the DB. The syntax is [role|user]:[role name|username] e.g. "role:ROLE_ADMIN" or "user:username".
 *
 * There are also two special entities that can be used for test user: {@link fi.vincit.multiusertest.annotation.TestUsers#CREATOR} and
 * {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER}. CREATOR uses the the same user as the resource was generated. NEW_USER
 * creates a new user with the same role as the creator had.
 */
public class MultiUserTestRunner extends Suite {

    private static final List<Runner> NO_RUNNERS = Collections
            .<Runner>emptyList();
    public static final String ROLE_PREFIX = "role:";
    public static final String USER_PREFIX = "user:";

    private final ArrayList<Runner> runners = new ArrayList<Runner>();

    private final static MultiUserTestClassRunnerFactory DEAFAULT_RUNNER_FACTORY = new MultiUserTestClassRunnerFactory() {
        @Override
        public ParentRunner<FrameworkMethod> createTestRunner(Class testClass, UserIdentifier creator, UserIdentifier user) throws InitializationError {
            return new SpringMultiUserTestClassRunner(testClass, creator, user);
        }
    };

    private MultiUserTestClassRunnerFactory testRunnerFactory = DEAFAULT_RUNNER_FACTORY;


    public MultiUserTestRunner(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        createRunnersForRoles(getCreatorRoles(), getUserRoles());
    }

    public MultiUserTestRunner(Class<?> klass, MultiUserTestClassRunnerFactory testRunnerFactory) throws Throwable {
        super(klass, NO_RUNNERS);
        setTestRunnerFactory(testRunnerFactory);
        createRunnersForRoles(getCreatorRoles(), getUserRoles());
    }


    public void setTestRunnerFactory(MultiUserTestClassRunnerFactory testRunnerFactory) {
        this.testRunnerFactory = testRunnerFactory;
    }

    private TestUsers getAnnotationOrThrow() throws Exception {
        TestUsers testRolesAnnotation = getTestClass().getJavaClass().getAnnotation(TestUsers.class);
        if (testRolesAnnotation != null) {
            return testRolesAnnotation;
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + getTestClass().getName()
                            + " Use " + TestUsers.class.getName() + " class"
            );
        }
    }

    private List<UserIdentifier> getCreatorRoles() throws Exception {
        return parseUserIdentifiers(getAnnotationOrThrow().creators());
    }

    private List<UserIdentifier> getUserRoles() throws Exception {
        return parseUserIdentifiers(getAnnotationOrThrow().users());
    }

    private List<UserIdentifier> parseUserIdentifiers(String[] creatorStrings) throws Exception {
        List<UserIdentifier> creators = new ArrayList<>(creatorStrings.length);
        for (String creatorString : creatorStrings) {
            creators.add(UserIdentifier.parse(creatorString));
        }
        return creators;
    }

    private void createRunnersForRoles(List<UserIdentifier> creatorIdentifiers, List<UserIdentifier> userIdentifiers) throws Exception {
        for (UserIdentifier creatorIdentifier : creatorIdentifiers) {
            for (UserIdentifier userIdentifier : userIdentifiers) {
                ParentRunner<FrameworkMethod> testRunner =
                        testRunnerFactory.createTestRunner(
                            getTestClass().getJavaClass(),
                            creatorIdentifier,
                            userIdentifier
                );
                runners.add(testRunner);
            }
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

}
