package fi.vincit.multiusertest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;

@TestUsers(creators = {"user:test-user"},
        users = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class InitCreatorBeforeTest extends ConfiguredTest {

    private static boolean creatorCreated = false;

    @Before
    public void init() {
        createUser("test-user", "Test", "User", User.Role.ROLE_USER, LoginRole.CREATOR);
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
        if (!getCreator().getUsername().equals("test-user")) {
            throw new AssertionError("Wrong creator user, should be test-user");
        }
    }

}
