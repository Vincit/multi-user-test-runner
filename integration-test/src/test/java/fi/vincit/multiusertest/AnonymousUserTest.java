package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.ANONYMOUS})
@RunWith(MultiUserTestRunner.class)
public class AnonymousUserTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

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
        assertThat(SecurityUtil.getLoggedInUser(), nullValue());
    }

    @Test
    public void producerLoggedInAfterUser() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        configuredTest.logInAs(LoginRole.PRODUCER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(),
                is(configuredTest.getProducer().getUsername()));
    }

    @Test
    public void expectFailureAnonymousUser() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);

        authorizationRule.given(() -> throwIfUserRole(RunWithUsers.ANONYMOUS))
                .whenCalledWithAnyOf(UserIdentifiers.anonymous())
                .then(expectException(IllegalStateException.class))
                .test();
    }

    private void throwIfUserRole(String identifier) {
        if (identifier.equals(RunWithUsers.ANONYMOUS)) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        } else {
            User.Role identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
            if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
                throw new IllegalStateException("Thrown when role was " + identifier);
            }
        }
    }

}
