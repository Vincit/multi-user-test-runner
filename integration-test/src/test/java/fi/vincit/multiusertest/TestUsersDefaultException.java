package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@MultiUserTestConfig(
        defaultException = IndexOutOfBoundsException.class
)
@RunWith(MultiUserTestRunner.class)
public class TestUsersDefaultException extends ConfiguredTest {

    @Test
    public void passes() {
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void fails() {
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
        throw new IndexOutOfBoundsException();
    }

    @Test(expected = IOException.class)
    public void fails_WithUnexpectedException() throws IOException {
        logInAs(LoginRole.USER);
        throw new IOException();
    }

    @Test
    public void fails_WithOverriddenException() throws IOException {
        authorization().setExpectedException(IllegalMonitorStateException.class);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
        throw new IllegalMonitorStateException();
    }
}
