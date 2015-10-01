package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;

@TestUsers(creators = {TestUsers.ANONYMOUS},
        users = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class AnonymousCreatorTest extends ConfiguredTest {

    @Test
    public void creatorLoggedIn() {
        assertThat(SecurityUtil.getLoggedInUser(), nullValue());
    }

    @Test
    public void userLoggedIn() {
        logInAs(LoginRole.USER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(getUser().getUsername()));
    }

    @Test
    public void creatorLoggedInAfterUser() {
        logInAs(LoginRole.USER);
        logInAs(LoginRole.CREATOR);

        assertThat(SecurityUtil.getLoggedInUser(), nullValue());
    }

    @Test
    public void expectFailureAnonymousCreator() {
        logInAs(LoginRole.CREATOR);
        authorization().expect(toFail(ifAnyOf(TestUsers.CREATOR)));
        throwIfUserRole(TestUsers.ANONYMOUS);
    }

    @Test
    public void dontExpectFailure() {
        authorization().dontExpectToFail();
    }

    private void throwIfUserRole(String identifier) {
        if (identifier.equals(TestUsers.ANONYMOUS)) {
            throw new IllegalStateException("Thrown when role was ANONYMOUS");
        } else {
            User.Role identifierRole = stringToRole(UserIdentifier.parse(identifier).getIdentifier());
            if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
                throw new IllegalStateException("Thrown when role was " + identifier);
            }
        }
    }

}
