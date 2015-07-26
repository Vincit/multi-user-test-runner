package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(creators = {"role:ROLE_ADMIN"}, users = "role:ROLE_USER",
        runner = BlockMultiUserTestClassRunner.class)
@RunWith(MultiUserTestRunner.class)
public class Java8SmokeTest extends ConfiguredTest {

    private TestService testService = new TestService();

    @Test(expected = AssertionError.class)
    public void expectCallToFail() throws Throwable {
        authorization().expect(call(testService::throwAccessDenied).toFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void expectNotToFail() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(
                call(testService::noThrow)
                        .notToFail(ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test
    public void expectNotToFail_Chained() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(
                call(testService::noThrow)
                    .toFail(ifAnyOf("role:ROLE_ADMIN"))
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
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                        .toAssert((value) -> assertThat(value, is(3)), ifAnyOf("role:ROLE_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(1)), ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectEqual_toFail() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                        .toEqual(1, ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test
    public void expectEqual_toPass() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(() -> testService.returnsValue(3))
                        .toEqual(3, ifAnyOf("role:ROLE_USER"))
        );
    }
}
