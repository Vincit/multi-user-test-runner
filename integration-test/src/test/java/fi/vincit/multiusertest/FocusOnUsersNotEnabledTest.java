package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.expectation.TestExpectations;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWithUsers(producers = {"$role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "$role:ROLE_USER", RunWithUsers.PRODUCER},
        focusEnabled = false)
@RunWith(MultiUserTestRunner.class)
public class FocusOnUsersNotEnabledTest {

    private static int numberOfCalls = 0;

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorization = new AuthorizationRule();

    @AfterClass
    public static void postCheck() {
        assertThat(numberOfCalls, is(6));
    }

    @Test
    public void onlyRunWithFocusedUsers() {
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
