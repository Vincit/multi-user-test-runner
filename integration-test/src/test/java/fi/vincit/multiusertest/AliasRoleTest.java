package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTestWithRoleAlias;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ADMIN", "role:NORMAL"},
        consumers = {"role:ADMIN", "role:NORMAL"})
@RunWith(MultiUserTestRunner.class)
public class AliasRoleTest {

    @MultiUserConfigClass
    private ConfiguredTestWithRoleAlias configuredTest = new ConfiguredTestWithRoleAlias();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();



    @Test
    public void producerLoggedIn() {
        assertThat(SecurityUtil.getLoggedInUser().getUsername(),
                is(configuredTest.getProducer().getUsername()));
    }

    @Test
    public void consumerLoggedIn() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(),
                is(configuredTest.getConsumer().getUsername()));
    }

    @Test
    public void expectFailureConsumer() throws Throwable {
        authorizationRule.given(() -> throwIfUserRole("role:NORMAL"))
                .whenCalledWithAnyOf(roles("NORMAL"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }
}
