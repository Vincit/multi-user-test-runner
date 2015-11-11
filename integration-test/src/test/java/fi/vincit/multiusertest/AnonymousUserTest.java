package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.ANONYMOUS})
@RunWith(MultiUserTestRunner.class)
public class AnonymousUserTest extends ConfiguredTest {

    @Test
    public void creatorLoggedIn() {
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(getCreator().getUsername()));
    }

    @Test
    public void userLoggedIn() {
        logInAs(LoginRole.CONSUMER);
        assertThat(SecurityUtil.getLoggedInUser(), nullValue());
    }

    @Test
    public void creatorLoggedInAfterUser() {
        logInAs(LoginRole.CONSUMER);
        logInAs(LoginRole.PRODUCER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(getCreator().getUsername()));
    }

    @Test
    public void expectFailureAnonymousUser() {
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf(RunWithUsers.ANONYMOUS)));
        throwIfUserRole(RunWithUsers.ANONYMOUS);
    }

    @Test
    public void dontExpectFailure() {
        authorization().dontExpectToFail();
    }

    private void throwIfUserRole(String identifier) {
        if (identifier.equals(RunWithUsers.ANONYMOUS)) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        } else {
            User.Role identifierRole = stringToRole(UserIdentifier.parse(identifier).getIdentifier());
            if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
                throw new IllegalStateException("Thrown when role was " + identifier);
            }
        }
    }

}
