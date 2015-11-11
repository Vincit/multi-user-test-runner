package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTestWithRoleAlias;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;

@RunWithUsers(producers = {"role:ADMIN", "role:NORMAL"},
        consumers = {"role:ADMIN", "role:NORMAL"})
@RunWith(MultiUserTestRunner.class)
public class AliasTestRole extends ConfiguredTestWithRoleAlias {

    @Test
    public void producerLoggedIn() {
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(getProducer().getUsername()));
    }

    @Test
    public void consumerLoggedIn() {
        logInAs(LoginRole.CONSUMER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(getConsumer().getUsername()));
    }

    @Test
    public void expectFailureCreator() {
        authorization().expect(toFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserIs(getProducer());
    }

    @Test
    public void expectFailureNewUser() {
        authorization().expect(toFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)));
        throwIfUserIs(getConsumer());
    }

    @Test
    public void expectFailureNotCreator() {
        authorization().expect(notToFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserIs(getConsumer());
    }

    @Test
    public void expectFailureNotNewUser() {
        authorization().expect(notToFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)));
        throwIfUserIs(getProducer());
    }

    @Test
    public void expectFailureUser() {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("role:NORMAL")));
        throwIfUserRole("role:NORMAL");
    }

    @Test
    public void dontExpectFailure() {
        authorization().dontExpectToFail();
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = stringToRole(UserIdentifier.parse(identifier).getIdentifier());
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
