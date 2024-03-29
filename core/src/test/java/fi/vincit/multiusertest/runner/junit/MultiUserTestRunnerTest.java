package fi.vincit.multiusertest.runner.junit;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MultiUserTestRunnerTest {

    @Ignore
    public static class NoAnnotation {
    }


    @Ignore
    public static class TestRunner extends ParentRunner<FrameworkMethod> {

        private Set<UserIdentifier> allowedIdentifiers;
        private UserIdentifier producer;
        private UserIdentifier consumer;
        private FocusType focusType;

        public TestRunner(RunnerConfig runnerConfig) throws InitializationError {
            super(runnerConfig.getTestClassType());
            this.allowedIdentifiers = runnerConfig.getAllowedIdentifiers();
            this.producer = runnerConfig.getProducerIdentifier();
            this.consumer = runnerConfig.getConsumerIdentifier();
            this.focusType = runnerConfig.getFocusType();
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

        public UserIdentifier getProducer() {
            return producer;
        }

        public UserIdentifier getConsumer() {
            return consumer;
        }
    }

    @Ignore
    public static class TestRunnerNoProperConstructor extends TestRunner {

        public TestRunnerNoProperConstructor(UserIdentifier producer) throws InitializationError {
            super(new RunnerConfig(TestRunnerNoProperConstructor.class, new HashSet<>(), producer, producer, FocusType.NONE));
        }
    }

    @RunWithUsers(producers = "role:ROLE_USERS")
    @MultiUserTestConfig(runner = TestRunnerNoProperConstructor.class)
    public static class NoProperConstructor {

    }

    @RunWithUsers(producers = "role:ROLE_USERS")
    @MultiUserTestConfig(runner = TestRunner.class)
    @Ignore
    public static class OneProducer {
    }

    @RunWithUsers(producers = {})
    @MultiUserTestConfig(runner = TestRunner.class)
    @Ignore
    public static class NoProducers {
    }

    @RunWithUsers(producers = RunWithUsers.PRODUCER)
    @MultiUserTestConfig(runner = TestRunner.class)
    @Ignore
    public static class ProducerProducer {
    }

    @RunWithUsers(producers = RunWithUsers.WITH_PRODUCER_ROLE)
    @MultiUserTestConfig(runner = TestRunner.class)
    @Ignore
    public static class UserWithProducerRole {
    }

    @RunWithUsers(producers = "role:ROLE_USERS", consumers = {"user:Foo", "role:Bar"})
    @MultiUserTestConfig(runner = TestRunner.class)
    @Ignore
    public static class OneProducer_MultipleUsers {
    }

    @RunWithUsers(producers = "user:username", consumers = RunWithUsers.WITH_PRODUCER_ROLE)
    @MultiUserTestConfig(runner = TestRunner.class)
    @Ignore
    public static class ExistingProducerNewUserWithProducerRole {
    }

    @Test(expected = IllegalStateException.class)
    public void testClassWithoutRunWithUsersAnnotation() throws Throwable {
        createMultiUserTestRunner(NoAnnotation.class);
    }

    @Test
    public void testClassWith_OneProducer_DefaultUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneProducer.class);

        assertThat(runner.getChildren().size(), is(1));
        TestRunner childRunner1 = (TestRunner) runner.getChildren().get(0);
        assertThat(childRunner1.getProducer(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner1.getConsumer(), is(UserIdentifier.getWithProducerRole()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassWith_InvalidProducerProducer() throws Throwable {
        createMultiUserTestRunner(ProducerProducer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassWith_InvalidUserWithProducerRole() throws Throwable {
        createMultiUserTestRunner(UserWithProducerRole.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassWith_NoProducer() throws Throwable {
        createMultiUserTestRunner(NoProducers.class);
    }

    @Test
    public void testClassWith_OneProducer_MultipleUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneProducer_MultipleUsers.class);


        assertThat(runner.getChildren().size(), is(2));

        TestRunner childRunner2 = (TestRunner) runner.getChildren().get(0);
        assertThat(childRunner2.getProducer(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner2.getConsumer(), is(UserIdentifier.parse("role:Bar")));

        TestRunner childRunner1 = (TestRunner) runner.getChildren().get(1);
        assertThat(childRunner1.getProducer(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner1.getConsumer(), is(UserIdentifier.parse("user:Foo")));

    }

    @Test(expected = NoSuchMethodException.class)
    public void testClassWith_NoProperConstructor() throws Throwable {
        createMultiUserTestRunner(NoProperConstructor.class);
    }

    @Test(expected = NoSuchMethodException.class)
    public void testClassWith_NotRunner() throws Throwable {
        createMultiUserTestRunner(NoProperConstructor.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testproducerHasExistingUserAndUsersHaveUserWithProducerRole() throws Throwable {
        createMultiUserTestRunner(ExistingProducerNewUserWithProducerRole.class);
    }

    private MultiUserTestRunner createMultiUserTestRunner(Class testClass) throws Throwable {
        return new MultiUserTestRunner(testClass);
    }

}
