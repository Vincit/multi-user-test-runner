package fi.vincit.multiusertest.junit5;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit5.Authorization;
import fi.vincit.multiusertest.runner.junit5.JUnit5MultiUserTestRunner;
import fi.vincit.multiusertest.util.MethodCalls;
import fi.vincit.multiusertest.util.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@ExtendWith(JUnit5MultiUserTestRunner.class)
public class JUnit5IgnoreMethodTest {

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

    @BeforeAll
    public static void initMethodCalls() {
        methodCalls.before();
    }

    @AfterAll
    public static void validateMethodCalls() {
        methodCalls.after();
        methodCalls.validateMethodCalls();
    }

    @TestTemplate
    @RunWithUsers(producers = {"role:ROLE_ADMIN"})
    public void runProducerAdmin(Authorization authorization) {
        methodCalls.call("runProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @TestTemplate
    @RunWithUsers(consumers = {"role:ROLE_USER"})
    public void runConsumerIsUser(Authorization authorization) {
        methodCalls.call("runConsumerIsUser");
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_USER));
    }

    @TestTemplate
    @RunWithUsers(consumers = {"role:ROLE_ADMIN"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerAdminAndProducerAdmin(Authorization authorization) {
        methodCalls.call("runConsumerAdminAndProducerAdmin");
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @TestTemplate
    @RunWithUsers(consumers = {"role:ROLE_USER"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerUserAndProducerAdmin(Authorization authorization) {
        methodCalls.call("runConsumerUserAndProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_USER));
    }

    @Disabled("JUnit 5 throws exception if no context available for a TestTemplate.")
    @TestTemplate
    @RunWithUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverRun(Authorization authorization) {
        methodCalls.call("neverRun");
        throw new AssertionError("Should never call this method");
    }

    @TestTemplate
    @IgnoreForUsers(consumers = {"role:ROLE_USER"}, producers = {"role:ROLE_ADMIN"})
    public void ignoreConsumerUserAndProducerAdmin(Authorization authorization) {
        methodCalls.call("ignoreConsumerUserAndProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_USER));
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @TestTemplate
    @IgnoreForUsers(consumers = {"role:ROLE_USER"})
    public void ignoreConsumerUser(Authorization authorization) {
        methodCalls.call("ignoreConsumerUser");
        assertThat(configuredTest.getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @TestTemplate
    @IgnoreForUsers(producers = {"role:ROLE_ADMIN"})
    public void ignoreProducerAdmin(Authorization authorization) {
        methodCalls.call("ignoreProducerAdmin");
        assertThat(configuredTest.getProducer().getRole(), is(User.Role.ROLE_USER));
    }

    @TestTemplate
    @IgnoreForUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverIgnore(Authorization authorization) {
        methodCalls.call("neverIgnore");
    }

    @TestTemplate
    @IgnoreForUsers
    public void neverIgnore_None(Authorization authorization) {
        methodCalls.call("neverIgnore_None");
    }


}
