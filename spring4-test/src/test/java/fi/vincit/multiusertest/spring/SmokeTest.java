package fi.vincit.multiusertest.spring;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.context.TestConfiguration;
import fi.vincit.multiusertest.runner.junit.framework.SpringMultiUserTestClassRunner;
import fi.vincit.multiusertest.spring.configuration.ConfiguredTest;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(
        creators = {"role:ROLE_ADMIN"}, users = "role:ROLE_USER",
        runner = SpringMultiUserTestClassRunner.class
)
@ContextConfiguration(classes = {TestConfiguration.class})
public class SmokeTest extends ConfiguredTest {

    @Test
    public void testNotFail() {
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void testFail() {
        logInAs(LoginRole.USER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER")));
        throw new AccessDeniedException("Denied");
    }
}
