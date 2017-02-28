package fi.vincit.multiusertest.junit5;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.runner.junit5.Authorization;
import fi.vincit.multiusertest.runner.junit5.JUnit5MultiUserTestRunner;
import fi.vincit.multiusertest.util.InitProducerBeforeTestConfiguredTest;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@RunWithUsers(producers = {"user:test-user"},
        consumers = {RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_USER"})
@ExtendWith(JUnit5MultiUserTestRunner.class)
public class JUnit5InitProducerBeforeTest {

    @MultiUserConfigClass
    private InitProducerBeforeTestConfiguredTest configuredTest =
            new InitProducerBeforeTestConfiguredTest();

    @BeforeEach
    public void init() {
        configuredTest.createUser("test-user", "Test", "Consumer", User.Role.ROLE_USER, LoginRole.PRODUCER);
        InitProducerBeforeTestConfiguredTest.setProducerCreated(true);
    }

    @TestTemplate
    public void producerLoggedIn(Authorization authorization) {
        if (!configuredTest.getProducer().getUsername().equals("test-user")) {
            throw new AssertionError("Wrong producer user, should be test-user");
        }
    }

    @TestTemplate
    public void logInAsUser(Authorization authorization) {
        configuredTest.logInAs(LoginRole.CONSUMER);
    }

}
