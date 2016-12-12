package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTestWithMultiUserAndRole;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Objects;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ADMIN:USER", "role:USER:VISITOR"},
        consumers = {"role:ADMIN:USER", "role:USER:VISITOR"})
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class MultiRoleTest {

    @MultiUserConfigClass
    private ConfiguredTestWithMultiUserAndRole configuredTest = new ConfiguredTestWithMultiUserAndRole();

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
    public void expectFailureProducer() {
        authorizationRule.expect(toFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserIs(configuredTest.getProducer());
    }

    @Test
    public void expectFailureWithProducerRole() {
        authorizationRule.expect(toFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)));
        throwIfUserIs(configuredTest.getConsumer());
    }

    @Test
    public void expectFailureNotProducer() {
        authorizationRule.expect(notToFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserIs(configuredTest.getConsumer());
    }

    @Test
    public void expectFailureNotWithProducerRole() {
        authorizationRule.expect(notToFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)));
        throwIfUserIs(configuredTest.getProducer());
    }

    @Test
    public void expectFailureConsumer() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:USER")));
        throwIfUserRole("role:USER");
    }

    @Test
    public void dontExpectFailure() {
        authorizationRule.dontExpectToFail();
    }

    private void throwIfUserRole(String identifier) {
        Collection<User.Role> identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (identifierRole.contains(SecurityUtil.getLoggedInUser().getRole())) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }

    private void throwIfUserIs(User user) {
        User loggedInUser = SecurityUtil.getLoggedInUser();
        Objects.requireNonNull(loggedInUser, "Logged in user must not be null");
        Objects.requireNonNull(user, "User must not be null");

        Objects.requireNonNull(loggedInUser.getUsername(), "Logged in user username must not be null");

        if (loggedInUser.getUsername().equals(user.getUsername())) {
            throw new IllegalStateException("Thrown when user was " + user);
        }
    }
}
