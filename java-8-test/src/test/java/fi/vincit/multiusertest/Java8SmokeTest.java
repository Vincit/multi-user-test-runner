package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_USER")
@RunWith(MultiUserTestRunner.class)
public class Java8SmokeTest extends ConfiguredTest {

    private TestService testService = new TestService();

    @Test(expected = AssertionError.class)
    public void expectCallToFail() throws Throwable {
        authorization().expect(call(testService::throwIllegalStateException).toFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void expectNotToFail() throws Throwable {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(
                call(testService::noThrow)
                        .notToFail(ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test
    public void expectAssert_toPass() throws Throwable {
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                .toAssert((value) -> assertThat(value, is(3)), ifAnyOf("role:ROLE_ADMIN"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectAssert_toFail() throws Throwable {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                        .toAssert((value) -> assertThat(value, is(3)), ifAnyOf("role:ROLE_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(1)), ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectEqual_toFail() throws Throwable {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                        .toEqual(1, ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test
    public void expectEqual_toPass() throws Throwable {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                        .toEqual(3, ifAnyOf("role:ROLE_USER"))
        );
    }
}
