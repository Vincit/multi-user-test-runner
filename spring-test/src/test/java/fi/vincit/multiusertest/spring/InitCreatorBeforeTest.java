package fi.vincit.multiusertest.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.context.TestConfiguration;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.spring.configuration.ConfiguredTest;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;

@RunWithUsers(producers = {"user:test-user"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@ContextConfiguration(classes = {TestConfiguration.class})
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
                throw new AssertionError("No producer created before logInAs call");
            }
        }
    }

    @Test
    public void producerLoggedIn() {
        if (!getProducer().getUsername().equals("test-user")) {
            throw new AssertionError("Wrong producer user, should be test-user");
        }
    }

}
