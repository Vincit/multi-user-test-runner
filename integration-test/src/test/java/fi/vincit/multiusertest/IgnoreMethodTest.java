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

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class IgnoreMethodTest extends ConfiguredTest {

    private final static int CLASS_CALLS_TOTAL_EXPECTED = 2 * 2;
    private static MethodCalls methodCalls = new MethodCalls(CLASS_CALLS_TOTAL_EXPECTED);

    @BeforeClass
    public static void initMethodCalls() {
        if (methodCalls.shouldInit()) {
            methodCalls.expectMethodCalls("runProducerAdmin", 2);
            methodCalls.expectMethodCalls("runConsumerIsUser", 2);
            methodCalls.expectMethodCalls("runConsumerAdminAndProducerAdmin", 1);
            methodCalls.expectMethodCalls("runConsumerUserAndProducerAdmin", 1);
            methodCalls.expectMethodCalls("neverRun", 0);
        }
        methodCalls.addClassCall();
    }

    @AfterClass
    public static void validateMethodCalls() {
        methodCalls.validateMethodCalls();
        methodCalls.validateClassCalls();
    }

    @Test
    @RunWithUsers(producers = {"role:ROLE_ADMIN"})
    public void runProducerAdmin() {
        methodCalls.call("runProducerAdmin");
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_USER"})
    public void runConsumerIsUser() {
        methodCalls.call("runConsumerIsUser");
        assertThat(getConsumerModel().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_ADMIN"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerAdminAndProducerAdmin() {
        methodCalls.call("runConsumerAdminAndProducerAdmin");
        assertThat(getConsumerModel().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_USER"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerUserAndProducerAdmin() {
        methodCalls.call("runConsumerUserAndProducerAdmin");
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getConsumerModel().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverRun() {
        methodCalls.call("neverRun");
        throw new AssertionError("Should never call this method");
    }

}
