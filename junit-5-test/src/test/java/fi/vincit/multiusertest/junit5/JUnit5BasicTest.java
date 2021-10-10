package fi.vincit.multiusertest.junit5;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.runner.junit5.JUnit5MultiUserTestRunner;
import fi.vincit.multiusertest.util.*;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@ExtendWith(JUnit5MultiUserTestRunner.class)
class JUnit5BasicTest {

    @MultiUserConfigClass
    private final ConfiguredTest configuredTest = new ConfiguredTest();

    @TestTemplate
    void producerLoggedIn(Authorization authorization) {
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getProducer().getUsername()));
    }

    @TestTemplate
    void consumerLoggedIn(Authorization authorization) throws Throwable {
        authorization.given(() -> {
            final User loggedInUser = SecurityUtil.getLoggedInUser();
            assertThat(loggedInUser, notNullValue());
            assertThat(SecurityUtil.getLoggedInUser().getUsername(),
                    CoreMatchers.is(configuredTest.getConsumer().getUsername()));
        })
                .byDefault(expectNotToFail())
                .test();
    }

    @TestTemplate
    void expectFailureConsumer(Authorization authorization) throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorization.given(() -> throwIfUserRole("role:ROLE_USER"))
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

    private void throwIfUserRoleIs(User user) {
        if (SecurityUtil.getLoggedInUser().getRole().equals(user.getRole())) {
            throw new IllegalStateException("Thrown when user was " + user);
        }
    }

}
