package fi.vincit.multiusertest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.User;

@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        runner = BlockMultiUserTestClassRunner.class)
@RunWith(MultiUserTestRunner.class)
public class CreatorsOnlyTest extends ConfiguredTest {


    @Test
    @TestUsers(creators = {"role:ROLE_ADMIN"})
    public void runCreatorAdmin() {
        assertThat(getCreatorRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @TestUsers(users = {"role:ROLE_USER"})
    public void runUserIsUser() {
        assertThat(getUserRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @TestUsers(users = {"role:ROLE_ADMIN"}, creators = {"role:ROLE_ADMIN"})
    public void runUserAdminAndCreatorAdmin() {
        assertThat(getUserRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getCreatorRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @TestUsers(users = {"role:ROLE_USER"}, creators = {"role:ROLE_ADMIN"})
    public void runUserUserAndCreatorAdmin() {
        assertThat(getUserRole(), is(User.Role.ROLE_USER));
        assertThat(getCreatorRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @TestUsers(users = {"role:ROLE_VISITOR"}, creators = {"role:ROLE_VISITOR"})
    public void neverRun() {
        throw new AssertionError("Should never call this method");
    }

}
