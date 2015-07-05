package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.runner.MultiUserTestRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        runner = BlockMultiUserTestClassRunner.class)
@RunWith(MultiUserTestRunner.class)
public class IgnoreMethodTest extends ConfiguredTest {


    @Test
    @TestUsers(creators = {"role:ROLE_ADMIN"})
    public void runCreatorAdmin() {
    }

    @Test
    @TestUsers(users = {"role:ROLE_USER"})
    public void runUserIsUser() {
    }

    @Test
    @TestUsers(users = {"role:ROLE_ADMIN"}, creators = {"role:ROLE_ADMIN"})
    public void runUserAdminAndCreatorAdmin() {
    }

    @Test
    @TestUsers(users = {"role:ROLE_USER"}, creators = {"role:ROLE_ADMIN"})
    public void runUserUserAndCreatorAdmin() {
    }

}
