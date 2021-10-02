package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.UserDefinitionClass;
import fi.vincit.multiusertest.rule.expectation.TestExpectations;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.User;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWithUsers(
        producerClass = FocusOnUsersWithClassTest.Producers.class,
        producers = {"$role:ROLE_ADMIN"},
        consumerClass = FocusOnUsersWithClassTest.Consumers.class,
        consumers = {"$role:ROLE_USER"},
        focusEnabled = true)
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class FocusOnUsersWithClassTest {

    public static class Producers implements UserDefinitionClass {
        @Override
        public String[] getUsers() {
            return new String[] {"role:ROLE_ADMIN", "role:ROLE_USER"};
        }
    }
    public static class Consumers implements UserDefinitionClass {
        @Override
        public String[] getUsers() {
            return new String[] {"role:ROLE_ADMIN", "role:ROLE_USER", RunWithUsers.PRODUCER};
        }
    }

    private static int numberOfCalls = 0;

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorization = new AuthorizationRule();

    @AfterClass
    public static void postCheck() {
        assertThat(numberOfCalls, is(1));
    }

    @Test
    public void onlyRunWithFocusedUsers() {
        if (configuredTest.getProducer().getRole() != User.Role.ROLE_ADMIN) {
            throw new AssertionError("Should not be run as producer with role: " + configuredTest.getProducer().getRole());
        }
        if (configuredTest.getConsumer().getRole() != User.Role.ROLE_USER) {
            throw new AssertionError("Should not be run as consumer with role: " + configuredTest.getConsumer().getRole());
        }
        ++numberOfCalls;
    }

    @Test
    public void tryUseNonFocusedUser() throws Throwable {
        authorization.given(() -> {})
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(TestExpectations.expectNotToFail())
                .test();
    }


}
