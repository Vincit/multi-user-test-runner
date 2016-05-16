package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.TestBaseClass;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Test;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.*;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
public class ComponentInheritanceTest extends TestBaseClass {

    @Test
    public void passes() {
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(notToFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void passes_users_roles_syntax() {
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(notToFail(ifAnyOf(roles("ROLE_ADMIN"), users("foo"))));
    }

    @Test
    public void fails() {
        expectFailAuthRule.setExpectedException(IllegalStateException.class);
        expectedException.expect(AssertionError.class);
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        expectFailAuthRule.expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
    }
}
