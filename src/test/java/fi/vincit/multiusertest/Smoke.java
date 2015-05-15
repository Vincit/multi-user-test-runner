package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.rule.Authentication;
import fi.vincit.multiusertest.runner.BlockMultiUserTestClassRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

@TestUsers(creators = {"role:ROLE_ADMIN"},
        runner = BlockMultiUserTestClassRunner.class)
public class Smoke extends ConfiguredTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(expectedException)
            .around(expectAuthenticationDeniedForUser);

    @Test
    public void passes() {
    }

    @Test
    public void fails() {
        expectedException.expect(AssertionError.class);
        authorization().expect(Authentication.toFail().ifAnyOf("role:ROLE_ADMIN"));
    }
}
