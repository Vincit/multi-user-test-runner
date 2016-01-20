package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.TestMultiUserConfig;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;
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
public class ComponentSmokeTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    public AuthorizationRule expectFailAuthRule = new AuthorizationRule();
    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @MultiUserConfigClass
    private MultiUserConfig<User, User.Role> multiUserConfig = new TestMultiUserConfig(authorizationRule);

    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(expectedException)
            .around(expectFailAuthRule);

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
