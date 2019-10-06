package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.assertValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFail;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectValue;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_USER")
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class JUnit5SmokeTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    private TestService testService = new TestService();


    @Test(expected = AssertionError.class)
    public void expectAssertionToFail() throws Throwable {
        authorizationRule.testCall(testService::throwAccessDenied)
                .byDefault(expectNotToFail())
                .test();
    }

    @Test
    public void expectCallNotToFail() throws Throwable {
        authorizationRule.testCall(testService::noThrow)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(expectNotToFail())
                .test();
    }

    @Test
    public void expectAssert_toPass() throws Throwable {
        authorizationRule.testCall(() -> testService.returnsValue(3))
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(assertValue(value -> assertThat(value, is(3))))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void expectAssert_toFail() throws Throwable {
        authorizationRule.testCall(() -> testService.returnsValue(3))
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(assertValue(value -> assertThat(value, is(1))))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void expectEqual_toFail() throws Throwable {
        authorizationRule.testCall(() -> testService.returnsValue(3))
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectValue(1))
                .test();
    }

    @Test
    public void expectEqual_toPass() throws Throwable {
        authorizationRule.testCall(() -> testService.returnsValue(3))
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectValue(3))
                .test();
    }

}
