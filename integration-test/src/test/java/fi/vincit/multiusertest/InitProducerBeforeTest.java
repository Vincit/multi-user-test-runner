package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.InitProducerBeforeTestConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWithUsers(producers = {"user:test-user"},
        consumers = {RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class InitProducerBeforeTest {

    @MultiUserConfigClass
    private InitProducerBeforeTestConfiguredTest configuredTest =
            new InitProducerBeforeTestConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();



    @Before
    public void init() {
        configuredTest.createUser("test-user", "Test", "Consumer", User.Role.ROLE_USER, LoginRole.PRODUCER);
        InitProducerBeforeTestConfiguredTest.setProducerCreated(true);
    }

    @Test
    public void producerLoggedIn() {
        if (!configuredTest.getProducer().getUsername().equals("test-user")) {
            throw new AssertionError("Wrong producer user, should be test-user");
        }
    }

    @Test
    public void logInAsUser() {
        configuredTest.logInAs(LoginRole.CONSUMER);
    }

}
