package fi.vincit.multiusertest.expectation2;

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
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectValue;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
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

    void throwOtherException() {
        throw new IllegalArgumentException("Some other exception that was expected");
    }

    private int returnValueCall() {
        return 103;
    }

    @Test
    public void expectToFail_WhenExpectedThrown() throws Throwable {
        authorizationRule.given(this::throwDefaultException)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_WhenSomethingNotExpectedThrown() throws Throwable {
        authorizationRule.given(this::throwOtherException)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void assertionFails_WithValue() throws Throwable {
        authorizationRule.given(this::returnValueCall)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectValue(104))
                .test();
    }

    @Test(expected = AssertionError.class)
    public void assertionFails_WithCustomAssertion() throws Throwable {
        authorizationRule.given(this::returnValueCall)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(assertValue(value -> fail()))
                .test();
    }

    @Test
    public void assertionPasses_WithValue() throws Throwable {
        authorizationRule.given(this::returnValueCall)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectValue(103))
                .test();
    }

    @Test
    public void assertionPasses_WithCustomerAssert() throws Throwable {
        authorizationRule.given(this::returnValueCall)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(assertValue(value -> assertThat(value, is(103))))
                .test();
    }

}
