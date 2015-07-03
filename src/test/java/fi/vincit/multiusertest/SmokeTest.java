package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.Authentication;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.runner.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@TestUsers(creators = {"role:ROLE_ADMIN"}, users = "role:ROLE_ADMIN",
        runner = BlockMultiUserTestClassRunner.class)
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
        authorization().expect(Authentication.notToFail().ifAnyOf("role:ROLE_ADMIN"));
    }

    @Test
    public void fails() {
        expectedException.expect(AssertionError.class);
        logInAs(LoginRole.USER);
        expectFailAuthRule.expect(Authentication.toFail().ifAnyOf("role:ROLE_ADMIN"));
    }
}
