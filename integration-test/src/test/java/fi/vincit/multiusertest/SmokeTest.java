package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.*;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@RunWith(MultiUserTestRunner.class)
public class SmokeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public AuthorizationRule expectFailAuthRule = new AuthorizationRule();

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();


    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(expectedException)
            .around(expectFailAuthRule);

    @Test
    public void passes() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        expectFailAuthRule.expect(notToFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test
    public void passes_users_roles_syntax() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        expectFailAuthRule.expect(notToFail(ifAnyOf(roles("ROLE_ADMIN"), users("foo"))));
    }

    @Test
    public void fails() {
        expectFailAuthRule.setExpectedException(IllegalStateException.class);
        expectedException.expect(AssertionError.class);
        configuredTest.logInAs(LoginRole.CONSUMER);
        expectFailAuthRule.expect(toFail(ifAnyOf("role:ROLE_ADMIN")));
    }
}
