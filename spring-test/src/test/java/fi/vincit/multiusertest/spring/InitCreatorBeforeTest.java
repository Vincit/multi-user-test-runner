package fi.vincit.multiusertest.spring;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.context.TestConfiguration;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.spring.configuration.ConfiguredTest;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@ContextConfiguration(classes = {TestConfiguration.class})
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
