package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.WITH_PRODUCER_ROLE})
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class NewUserTest {

    @MultiUserConfigClass
    public ConfiguredTest configuredTest = new ConfiguredTest();

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
    public void producerLoggedInAfterUser() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        configuredTest.logInAs(LoginRole.PRODUCER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(),
                is(configuredTest.getProducer().getUsername()));
    }

    @Test
    public void expectFailureProducer() {
        authorizationRule.expect(toFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserIs(configuredTest.getProducer());
    }

    @Test
    public void expectFailureUserWithProducerRole() {
        authorizationRule.expect(toFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)));
        throwIfUserIs(configuredTest.getConsumer());
    }

    @Test
    public void expectFailureNotProducer() {
        authorizationRule.expect(notToFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserIs(configuredTest.getConsumer());
    }

    @Test
    public void expectFailureNotUserWithProducerRole() {
        authorizationRule.expect(notToFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)));
        throwIfUserIs(configuredTest.getProducer());
    }

    @Test
    public void expectFailureConsumer() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:ROLE_USER")));
        throwIfUserRole("role:ROLE_USER");
    }

    @Test
    public void dontExpectFailure() {
        authorizationRule.dontExpectToFail();
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }

    private void throwIfUserIs(User user) {
        if (SecurityUtil.getLoggedInUser().getUsername().equals(user.getUsername())) {
            throw new IllegalStateException("Thrown when user was " + user);
        }
    }

}
