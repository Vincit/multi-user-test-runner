package fi.vincit.multiusertest.runner.junit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.UserIdentifier;

public class MultiUserTestRunnerTest {

    @Ignore
    public static class NoAnnotation {
    }


    @Ignore
    public static class TestRunner extends ParentRunner<FrameworkMethod> {

        private UserIdentifier producer;
        private UserIdentifier consumer;

        public TestRunner(Class<?> clazz, UserIdentifier producer, UserIdentifier consumer) throws InitializationError {
            super(clazz);
            this.producer = producer;
            this.consumer = consumer;
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
            super(TestRunnerNoProperConstructor.class, producer, producer);
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
    public static class ProdcuerProducer {
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
    public void testClassWith_InvalidProdcuerProducer() throws Throwable {
        createMultiUserTestRunner(ProdcuerProducer.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassWith_InvalidUserWithProducerRole() throws Throwable {
        createMultiUserTestRunner(UserWithProducerRole.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassWith_NoProdcuer() throws Throwable {
        createMultiUserTestRunner(NoProducers.class);
    }

    @Test
    public void testClassWith_OneProducer_MultipleUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneProducer_MultipleUsers.class);


        assertThat(runner.getChildren().size(), is(2));

        TestRunner childRunner1 = (TestRunner) runner.getChildren().get(0);
        assertThat(childRunner1.getProducer(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner1.getConsumer(), is(UserIdentifier.parse("user:Foo")));

        TestRunner childRunner2 = (TestRunner) runner.getChildren().get(1);
        assertThat(childRunner2.getProducer(), is(UserIdentifier.parse("role:ROLE_USERS")));
        assertThat(childRunner2.getConsumer(), is(UserIdentifier.parse("role:Bar")));

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
