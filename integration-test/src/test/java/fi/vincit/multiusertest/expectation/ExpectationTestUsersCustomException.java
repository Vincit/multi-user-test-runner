package fi.vincit.multiusertest.expectation;

import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.LoginRole;

@RunWithUsers(producers = "role:ROLE_USER", consumers = "role:ROLE_USER")
@MultiUserTestConfig(
        runner = BlockMultiUserTestClassRunner.class,
        defaultException = IndexOutOfBoundsException.class
)
@RunWith(MultiUserTestRunner.class)
public class ExpectationTestUsersCustomException extends ConfiguredTest {

    void throwDefaultException() throws Throwable {
        throw new IndexOutOfBoundsException();
    }

    private int returnValueCall() {
        return 103;
    }

    @Test
    public void expectToFail_AsProducer_WhenAccessDeniedThrown() throws Throwable {
        authorization().expect(Expectations.call(
                new FunctionCall() {
                    @Override
                    public void call() throws Throwable {
                        throwDefaultException();
                    }
                }
        ).toFail(ifAnyOf(RunWithUsers.PRODUCER)));
    }

    @Test(expected = AssertionError.class)
    public void expectNotToFail_AsProducer_WhenAccessDeniedThrown() throws Throwable {
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
        logInAs(LoginRole.CONSUMER);
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
        logInAs(LoginRole.CONSUMER);
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
        logInAs(LoginRole.CONSUMER);
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
        logInAs(LoginRole.CONSUMER);
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
