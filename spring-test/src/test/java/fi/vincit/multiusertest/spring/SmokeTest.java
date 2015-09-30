package fi.vincit.multiusertest.spring;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.context.TestConfiguration;
import fi.vincit.multiusertest.spring.configuration.ConfiguredTest;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(
        creators = {"role:ROLE_ADMIN"}, users = {"role:ROLE_USER", TestUsers.UNREGISTERED}
)
@ContextConfiguration(classes = {TestConfiguration.class})
public class SmokeTest extends ConfiguredTest {

    @Test
    public void testNotFail() {
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_USER", TestUsers.UNREGISTERED)));
    }

    @Test
    public void testFail() {
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER", TestUsers.UNREGISTERED)));
        throw new AccessDeniedException("Denied");
    }

    @Test
    public void testFail_CustomException() throws IOException {
        authorization().setExpectedException(IOException.class);
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER", TestUsers.UNREGISTERED)));
        throw new IOException("IO Fail");
    }
}
