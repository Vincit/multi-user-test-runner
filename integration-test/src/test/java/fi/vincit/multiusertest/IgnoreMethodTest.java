package fi.vincit.multiusertest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.User;

@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class IgnoreMethodTest extends ConfiguredTest {

    @Test
    @TestUsers(creators = {"role:ROLE_ADMIN"})
    public void runCreatorAdmin() {
        assertThat(getCreatorModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @TestUsers(users = {"role:ROLE_USER"})
    public void runUserIsUser() {
        assertThat(getUserModel().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @TestUsers(users = {"role:ROLE_ADMIN"}, creators = {"role:ROLE_ADMIN"})
    public void runUserAdminAndCreatorAdmin() {
        assertThat(getUserModel().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getCreatorModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @TestUsers(users = {"role:ROLE_USER"}, creators = {"role:ROLE_ADMIN"})
    public void runUserUserAndCreatorAdmin() {
        assertThat(getCreatorModel().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getUserModel().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @TestUsers(users = {"role:ROLE_VISITOR"}, creators = {"role:ROLE_VISITOR"})
    public void neverRun() {
        throw new AssertionError("Should never call this method");
    }

}
