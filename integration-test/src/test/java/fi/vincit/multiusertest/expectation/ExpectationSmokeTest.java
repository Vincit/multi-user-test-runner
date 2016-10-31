package fi.vincit.multiusertest.expectation;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
        authorizationRule.expect(Expectations.call(
                this::throwDefaultException
        ).toFail(ifAnyOf(RunWithUsers.PRODUCER)));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_AsProducer_WhenAccessDeniedThrown() throws Throwable {
        authorizationRule.expect(Expectations.call(
                this::throwDefaultException
        ).toFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void expectToFail_WhenAccessDeniedThrown() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(Expectations.call(
                this::throwDefaultException
        ).toFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_WhenAccessDeniedThrown() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(Expectations.call(
                this::throwDefaultException
        ).toFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test(expected = AssertionError.class)
    public void assertionFails() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(
                this::returnValueCall
        ).toAssert(value ->
                assertThat(value, is(104)), ifAnyOf("role:ROLE_USER"))
        );
    }

    @Test
    public void assertionPasses() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(
                this::returnValueCall
        ).toAssert(value ->
                assertThat(value, is(103)), ifAnyOf("role:ROLE_USER"))
        );
    }

}
