package fi.vincit.multiusertest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class InitCreatorBeforeTest extends ConfiguredTest {

    private static boolean creatorCreated = false;

    @Before
    public void init() {
        creatorCreated = true;
    }

    @Override
    public void logInAs(LoginRole role) {
        if (role == LoginRole.CREATOR) {
            if (!creatorCreated) {
                throw new AssertionError("No creator created before logInAs call");
            }
        }
    }

    @Test
    public void creatorLoggedIn() {
    }


}
