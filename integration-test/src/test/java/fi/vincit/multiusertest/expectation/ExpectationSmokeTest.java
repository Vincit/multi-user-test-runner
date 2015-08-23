package fi.vincit.multiusertest.expectation;

import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;

@TestUsers(creators = "role:ROLE_USER", users = "role:ROLE_USER")
@RunWith(MultiUserTestRunner.class)
public class ExpectationSmokeTest extends ConfiguredTest {

    void throwDefaultException() {
        throw new IllegalStateException("Denied");
    }

    private int returnValueCall() {
        return 103;
    }

    @Test
    public void expectToFail_AsCreator_WhenAccessDeniedThrown() throws Throwable {
        authorization().expect(Expectations.call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwDefaultException();
                    }
                }
        ).toFail(ifAnyOf(TestUsers.CREATOR)));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_AsCreator_WhenAccessDeniedThrown() throws Throwable {
        authorization().expect(Expectations.call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwDefaultException();
                    }
                }
        ).toFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void expectToFail_WhenAccessDeniedThrown() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(Expectations.call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwDefaultException();
                    }
                }
        ).toFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_WhenAccessDeniedThrown() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(Expectations.call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwDefaultException();
                    }
                }
        ).toFail(ifAnyOf("role:ROLE_ADMIN")));
    }

    @Test(expected = AssertionError.class)
    public void assertionFails() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(
                new ReturnValueCall<Integer>() {
                    @Override
                    public Integer call() {
                        return returnValueCall();
                    }
                }
        ).toAssert(new AssertionCall<Integer>() {
            @Override
            public void call(Integer value) {
                assertThat(value, is(104));
            }
        }, ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void assertionPasses() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(valueOf(
                new ReturnValueCall<Integer>() {
                    @Override
                    public Integer call() {
                        return returnValueCall();
                    }
                }
        ).toAssert(new AssertionCall<Integer>() {
            @Override
            public void call(Integer value) {
                assertThat(value, is(103));
            }
        }, ifAnyOf("role:ROLE_USER")));
    }

}
