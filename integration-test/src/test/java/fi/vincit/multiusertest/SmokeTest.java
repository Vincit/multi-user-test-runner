package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@RunWith(MultiUserTestRunner.class)
public class SmokeTest extends ConfiguredTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    public AuthorizationRule expectFailAuthRule = new AuthorizationRule();

    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(expectedException)
            .around(expectFailAuthRule);

    @Test
    public void passes() {
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void passes_users_roles_syntax() {
        logInAs(LoginRole.USER);
        authorization().expect(notToFail(ifAnyOf(roles("ROLE_ADMIN"), users("foo"))));
    }

    @Test
    public void fails() {
        expectFailAuthRule.setExpectedException(IllegalStateException.class);
        expectedException.expect(AssertionError.class);
        logInAs(LoginRole.USER);
        expectFailAuthRule.expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
    }
}
