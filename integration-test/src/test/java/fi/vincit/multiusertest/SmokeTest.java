package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.expectation.TestExpectations;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
@RunWith(MultiUserTestRunner.class)
public class SmokeTest {

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    private void callPass() {
    }

    private void callFails() {
        throw new IllegalStateException();
    }

    @Test
    public void passes() throws Throwable {
        authorizationRule.testCall(this::callPass)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(TestExpectations.expectNotToFail())
                .test();
    }

    @Test
    public void passes_users_roles_syntax() throws Throwable {
        authorizationRule.testCall(this::callPass)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), users("foo"))
                .then(TestExpectations.expectNotToFail())
                .test();
    }

    @Test
    public void fails() throws Throwable {
        authorizationRule.testCall(this::callFails)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(TestExpectations.expectException(IllegalStateException.class))
                .test();
    }
}
