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
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@RunWith(MultiUserTestRunner.class)
public class ComponentSmokeTest {

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @MultiUserConfigClass
    private MultiUserConfig<User, User.Role> multiUserConfig = new TestMultiUserConfig();


    public void pass() {
    }

    public void fail() {
        throw new IllegalStateException();
    }

    @Test
    public void passes() throws Throwable {
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::pass)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(expectNotToFail())
                .test();
    }

    @Test
    public void passes_users_roles_syntax() throws Throwable {
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::pass)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), users("foo"))
                .then(expectNotToFail())
                .test();
    }

    @Test
    public void fails() throws Throwable {
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::fail)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(expectException(IllegalStateException.class))
                .test();
    }
}
