package fi.vincit.multiusertest.junit5;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit5.Authorization;
import fi.vincit.multiusertest.runner.junit5.JUnit5MultiUserTestRunner;
import fi.vincit.multiusertest.util.*;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@ExtendWith(JUnit5MultiUserTestRunner.class)
public class JUnit5BasicTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @TestTemplate
    public void producerLoggedIn(Authorization authorization) {
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getProducer().getUsername()));
    }

    @TestTemplate
    public void consumerLoggedIn(Authorization authorization) {
        configuredTest.logInAs(LoginRole.CONSUMER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getConsumer().getUsername()));
    }

    @TestTemplate
    public void producerLoggedInAfterConsumer(Authorization authorization) {
        configuredTest.logInAs(LoginRole.CONSUMER);
        configuredTest.logInAs(LoginRole.PRODUCER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getProducer().getUsername()));
    }

    @TestTemplate
    public void expectFailureProducer(Authorization authorization) throws Throwable {
        authorization.testCall(() -> throwIfUserIs(configuredTest.getProducer()))
                .whenCalledWithAnyOf(UserIdentifiers.producer())
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @TestTemplate
    public void expectFailureWithProducerRole(Authorization authorization) throws Throwable {
        authorization.testCall(() -> throwIfUserIs(configuredTest.getConsumer()))
                .whenCalledWithAnyOf(UserIdentifiers.withProducerRole())
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @TestTemplate
    public void expectFailureConsumer(Authorization authorization) throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorization.testCall(() -> throwIfUserRole("role:ROLE_USER"))
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }

    private void throwIfUserIs(User user) {
        if (SecurityUtil.getLoggedInUser().getUsername().equals(user.getUsername())) {
            throw new IllegalStateException("Thrown when user was " + user);
        }
    }

}
