package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.util.User;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

@RunWith(MultiUserTestRunner.class)
public abstract class TestBaseClass {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    public AuthorizationRule expectFailAuthRule = new AuthorizationRule();
    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @MultiUserConfigClass
    public MultiUserConfig<User, User.Role> multiUserConfig = new TestMultiUserConfig();

    @Rule
    public RuleChain ruleChain = RuleChain
            .outerRule(expectedException)
            .around(expectFailAuthRule);


}
