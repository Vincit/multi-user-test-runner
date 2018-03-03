package fi.vincit.multiusertest.expectation2;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.*;
import static fi.vincit.multiusertest.util.UserIdentifiers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWithUsers(producers = "role:ROLE_USER", consumers = "role:ROLE_USER")
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class ExpectationSmokeTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();
    
    void throwDefaultException() {
        throw new IllegalStateException("Denied");
    }

    private int returnValueCall() {
        return 103;
    }

    @Test
    public void expectToFail_AsProducer_WhenAccessDeniedThrown() throws Throwable {
        authorizationRule.testCall(this::throwDefaultException)
                .whenCalledWith(anyOf(RunWithUsers.PRODUCER))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_AsProducer_WhenAccessDeniedThrown() throws Throwable {
        authorizationRule.testCall(this::throwDefaultException)
                .whenCalledWith(anyOf("role:ROLE_USER"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test
    public void expectToFail_WhenAccessDeniedThrown() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.testCall(this::throwDefaultException)
                .whenCalledWith(anyOf("role:ROLE_USER"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_WhenAccessDeniedThrown() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.testCall(this::throwDefaultException)
                .whenCalledWith(anyOf("role:ROLE_ADMIN"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void assertionFails_WithValue() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::returnValueCall)
                .whenCalledWith(anyOf("role:ROLE_USER"))
                .then(expectValue(104))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void assertionFails_WithCustomAssertion() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::returnValueCall)
                .whenCalledWith(anyOf("role:ROLE_USER"))
                .then(assertValue(value -> fail()))
                .test();
    }

    @Test
    public void assertionPasses_WithValue() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::returnValueCall)
                .whenCalledWith(anyOf("role:ROLE_USER"))
                .then(expectValue(103))
                .test();
    }

    @Test
    public void assertionPasses_WithCustomerAssert() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::returnValueCall)
                .whenCalledWith(anyOf("role:ROLE_USER"))
                .then(assertValue(value -> assertThat(value, is(103))))
                .test();
    }

}
