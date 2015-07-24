package fi.vincit.multiusertest.expectation;

import static fi.vincit.multiusertest.rule.Authentication.ifAnyOf;
import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.expection.Callback;
import fi.vincit.multiusertest.rule.expection.ExpectValueOfCallback;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(creators = "role:ROLE_USER", users = "role:ROLE_USER",
        runner = BlockMultiUserTestClassRunner.class)
@RunWith(MultiUserTestRunner.class)
public class ExpectationSmokeTest extends ConfiguredTest {

    void throwAccessDenied() {
        throw new AccessDeniedException("Denied");
    }

    private int returnValueCall() {
        return 103;
    }

    @Test
    public void expectToFail_AsCreator_WhenAccessDeniedThrown() throws Throwable {
        authorization().expect(call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwAccessDenied();
                    }
                }
        ).toFail(ifAnyOf(TestUsers.CREATOR)));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_AsCreator_WhenAccessDeniedThrown() throws Throwable {
        authorization().expect(call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwAccessDenied();
                    }
                }
        ).toFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void expectToFail_WhenAccessDeniedThrown() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwAccessDenied();
                    }
                }
        ).toFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_WhenAccessDeniedThrown() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwAccessDenied();
                    }
                }
        ).toFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test(expected = AssertionError.class)
    public void assertionFails() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(
                new ExpectValueOfCallback<Integer>() {
                    @Override
                    public Integer doIt() {
                        return returnValueCall();
                    }
                }
        ).toAssert(new Callback<Integer>() {
            @Override
            public void doIt(Integer value) {
                assertThat(value, is(104));
            }
        }, ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void assertionPasses() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(
                new ExpectValueOfCallback<Integer>() {
                    @Override
                    public Integer doIt() {
                        return returnValueCall();
                    }
                }
        ).toAssert(new Callback<Integer>() {
            @Override
            public void doIt(Integer value) {
                assertThat(value, is(103));
            }
        }, ifAnyOf("role:ROLE_USER")));
    }

}
