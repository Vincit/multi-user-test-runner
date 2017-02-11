package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.MethodCalls;
import fi.vincit.multiusertest.util.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@MultiUserTestConfig
@RunWith(MultiUserTestRunner.class)
public class IgnoreMethodTest {

    private static MethodCalls methodCalls = new MethodCalls()
            .expectMethodCalls("neverIgnore", 4)
            .expectMethodCalls("neverIgnore_None", 4)
            .expectMethodCalls("runProducerAdmin", 2)
            .expectMethodCalls("runConsumerIsUser", 2)
            .expectMethodCalls("ignoreProducerAdmin", 2)
            .expectMethodCalls("ignoreConsumerUser", 2)
            .expectMethodCalls("runConsumerAdminAndProducerAdmin", 1)
            .expectMethodCalls("runConsumerUserAndProducerAdmin", 1)
            .expectMethodCalls("ignoreConsumerUserAndProducerAdmin", 1)
            .expectMethodCalls("neverRun", 0);

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @BeforeClass
    public static void initMethodCalls() {
        methodCalls.before();
    }

    @AfterClass
    public static void validateMethodCalls() {
        methodCalls.after();
        methodCalls.validateMethodCalls();
    }

    @Test
    @RunWithUsers(producers = {"role:ROLE_ADMIN"})
    public void runProducerAdmin() {
        methodCalls.call("runProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_USER"})
    public void runConsumerIsUser() {
        methodCalls.call("runConsumerIsUser");
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_ADMIN"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerAdminAndProducerAdmin() {
        methodCalls.call("runConsumerAdminAndProducerAdmin");
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_USER"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerUserAndProducerAdmin() {
        methodCalls.call("runConsumerUserAndProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverRun() {
        methodCalls.call("neverRun");
        throw new AssertionError("Should never call this method");
    }

    @Test
    @IgnoreForUsers(consumers = {"role:ROLE_USER"}, producers = {"role:ROLE_ADMIN"})
    public void ignoreConsumerUserAndProducerAdmin() {
        methodCalls.call("ignoreConsumerUserAndProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_USER));
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @IgnoreForUsers(consumers = {"role:ROLE_USER"})
    public void ignoreConsumerUser() {
        methodCalls.call("ignoreConsumerUser");
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @IgnoreForUsers(producers = {"role:ROLE_ADMIN"})
    public void ignoreProducerAdmin() {
        methodCalls.call("ignoreProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @IgnoreForUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverIgnore() {
        methodCalls.call("neverIgnore");
    }

    @Test
    @IgnoreForUsers
    public void neverIgnore_None() {
        methodCalls.call("neverIgnore_None");
    }


}
