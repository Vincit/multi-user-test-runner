package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTestWithCustomRunnerAndException;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@RunWith(MultiUserTestRunner.class)
public class TestUsersCustomRunnerAndException extends ConfiguredTestWithCustomRunnerAndException {

    @Test
    public void passes() {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void fails() {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
        throw new IllegalArgumentException();
    }

    @Test(expected = IOException.class)
    public void fails_WithUnexpectedException() throws IOException {
        logInAs(LoginRole.CONSUMER);
        throw new IOException();
    }

    @Test
    public void fails_WithOverriddenException() throws IOException {
        authorization().setExpectedException(IllegalMonitorStateException.class);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
        throw new IllegalMonitorStateException();
    }
}
