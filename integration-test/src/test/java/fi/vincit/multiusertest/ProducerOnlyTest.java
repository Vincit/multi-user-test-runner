package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.MethodCalls;
import fi.vincit.multiusertest.util.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class ProducerOnlyTest extends ConfiguredTest {

    private static MethodCalls methodCalls = new MethodCalls()
            .expectMethodCalls("runWithEverything", 2)
            .expectMethodCalls("runProducerAdmin", 1)
            .expectMethodCalls("runConsumerAndProducerAdmin", 1)
            .expectMethodCalls("runConsumerAndProducerUser", 1)
            .expectMethodCalls("runConsumerUserAndProducerAdmin", 0)
            .expectMethodCalls("neverRun", 0);

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
    @RunWithUsers()
    public void runWithEverything() {
        methodCalls.call("runWithEverything");
    }

    @Test
    @RunWithUsers(producers = {"role:ROLE_ADMIN"})
    public void runProducerAdmin() {
        methodCalls.call("runProducerAdmin");
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = {"role:ROLE_ADMIN"})
    public void runConsumerAndProducerAdmin() {
        methodCalls.call("runConsumerAndProducerAdmin");
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getConsumer().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(producers = {"role:ROLE_USER"}, consumers = {"role:ROLE_USER"})
    public void runConsumerAndProducerUser() {
        methodCalls.call("runConsumerAndProducerUser");
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_USER));
        assertThat(getConsumer().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = {"role:ROLE_USER"})
    public void runConsumerUserAndProducerAdmin() {
        methodCalls.call("runConsumerUserAndProducerAdmin");
    }


    @Test
    @RunWithUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverRun() {
        methodCalls.call("neverRun");
        throw new AssertionError("Should never call this method");
    }

}
