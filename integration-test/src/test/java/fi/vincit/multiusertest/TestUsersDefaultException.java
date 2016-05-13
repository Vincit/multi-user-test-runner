package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@MultiUserTestConfig(
        defaultException = IndexOutOfBoundsException.class
)
@RunWith(MultiUserTestRunner.class)
public class TestUsersDefaultException {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Test
    public void passes() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(notToFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void fails() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
        throw new IndexOutOfBoundsException();
    }

    @Test(expected = IOException.class)
    public void fails_WithUnexpectedException() throws IOException {
        configuredTest.logInAs(LoginRole.CONSUMER);
        throw new IOException();
    }

    @Test
    public void fails_WithOverriddenException() throws IOException {
        authorizationRule.setExpectedException(IllegalMonitorStateException.class);
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
        throw new IllegalMonitorStateException();
    }
}
