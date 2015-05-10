package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MultiUserTestRunnerTest {

    @Ignore
    public static class NoAnnotation {
    }


    @Ignore
    public static class TestRunner extends ParentRunner<FrameworkMethod> {

        private UserIdentifier creator;
        private UserIdentifier user;

        public TestRunner(Class<?> clazz, UserIdentifier creator, UserIdentifier user) throws InitializationError {
            super(clazz);
            this.creator = creator;
            this.user = user;
        }

        @Override
        protected List<FrameworkMethod> getChildren() {
            return Collections.emptyList();
        }

        @Override
        protected Description describeChild(FrameworkMethod frameworkMethod) {
            return Description.createSuiteDescription("Test class");
        }

        @Override
        protected void runChild(FrameworkMethod frameworkMethod, RunNotifier runNotifier) {
        }

        public UserIdentifier getCreator() {
            return creator;
        }

        public UserIdentifier getUser() {
            return user;
        }
    }

    @TestUsers(creators = "role:ROLE_USERS", runner = TestRunner.class)
    @Ignore
    public static class OneCreator {
    }

    @TestUsers(creators = "role:ROLE_USERS", users = {"user:Foo", "role:Bar"}, runner = TestRunner.class)
    @Ignore
    public static class OneCreator_MultipleUsers {
    }

    @Test(expected = IllegalStateException.class)
    public void testClassWithoutTestUsersAnnotation() throws Throwable {
        createMultiUserTestRunner(NoAnnotation.class);
    }

    @Test
    public void testClassWith_OneCreator_DefaultUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneCreator.class);

        assertThat(runner.getChildren().size(), is(1));
        TestRunner childRunner1 = (TestRunner) runner.getChildren().get(0);
        assertThat(childRunner1.getCreator(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner1.getUser(), is(UserIdentifier.getNewUser()));
    }

    @Test
    public void testClassWith_OneCreator_MultipleUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneCreator_MultipleUsers.class);


        assertThat(runner.getChildren().size(), is(2));

        TestRunner childRunner1 = (TestRunner) runner.getChildren().get(0);
        assertThat(childRunner1.getCreator(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner1.getUser(), is(UserIdentifier.parse("user:Foo")));

        TestRunner childRunner2 = (TestRunner) runner.getChildren().get(1);
        assertThat(childRunner2.getCreator(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner2.getUser(), is(UserIdentifier.parse("role:Bar")));

    }

    private MultiUserTestRunner createMultiUserTestRunner(Class testClass) throws Throwable {
        return new MultiUserTestRunner(testClass);
    }

}
