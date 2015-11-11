package fi.vincit.multiusertest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;

@RunWithUsers(producers = {"user:test-user"},
        consumers = {RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class InitCreatorBeforeTest extends ConfiguredTest {

    private static boolean producerCreated = false;

    @Before
    public void init() {
        createUser("test-user", "Test", "User", User.Role.ROLE_USER, LoginRole.PRODUCER);
        producerCreated = true;
    }

    @Override
    public void logInAs(LoginRole role) {
        if (role == LoginRole.PRODUCER) {
            if (!producerCreated) {
                throw new AssertionError("No prducer created before logInAs call");
            }
        } else {
            super.logInAs(role);
        }
    }

    @Test
    public void producerLoggedIn() {
        if (!getProducer().getUsername().equals("test-user")) {
            throw new AssertionError("Wrong producer user, should be test-user");
        }
    }

    @Test
    public void logInAsUser() {
        logInAs(LoginRole.CONSUMER);
    }

}
