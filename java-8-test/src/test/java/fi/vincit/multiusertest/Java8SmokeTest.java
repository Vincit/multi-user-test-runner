package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

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
}
