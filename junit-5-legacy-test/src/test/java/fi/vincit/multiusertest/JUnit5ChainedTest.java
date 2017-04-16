package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWithUsers(producers = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", },
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_VISITOR"})
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class JUnit5ChainedTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    private TestService testService = new TestService();

    @Test
    @Ignore("TODO")
    public void test() {
    }

    /*
    @Test
    public void expectAssert_toPass() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(2);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> testService.returnsValue(4);
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                        .toAssert((value) -> assertThat(value, is(1)), ifAnyOf("role:ROLE_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(2)), ifAnyOf("role:ROLE_USER"))
                        .toAssert((value) -> assertThat(value, is(3)), ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(4)), ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectAssert_toFail() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(2);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> testService.returnsValue(4);
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                        .toAssert((value) -> assertThat(value, is(91)), ifAnyOf("role:ROLE_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(92)), ifAnyOf("role:ROLE_USER"))
                        .toAssert((value) -> assertThat(value, is(93)), ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(94)), ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    public void expectAssert_toFailWithException_toPass() throws Throwable {
        ExpectCall expectValueOf = call(testService::throwAccessDenied);

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(expectValueOf
                        .toFailWithException(
                                IllegalStateException.class,
                                ifAnyOf("role:ROLE_ADMIN"),
                                exception -> {
                                    assertThat(exception.getMessage(), is("Denied"));
                                }
                        )
        );
    }

    @Test(expected = AssertionError.class)
    public void expectAssert_toFailWithException_toFail() throws Throwable {
        ExpectCall expectValueOf = call(() -> testService.throwAccessDenied());

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(expectValueOf
                        .toFailWithException(
                                IllegalStateException.class,
                                ifAnyOf("role:ROLE_ADMIN"),
                                exception -> {
                                    assertThat(exception.getMessage(), is("Foo"));
                                }
                        )
        );
    }

    @Test
    public void expectToEqual_toPass() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(2);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> testService.returnsValue(4);
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                        .toEqual(1, ifAnyOf("role:ROLE_ADMIN"))
                        .toEqual(2, ifAnyOf("role:ROLE_USER"))
                        .toEqual(3, ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toEqual(4, ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectToEqual_toFail() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(2);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> testService.returnsValue(4);
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                        .toEqual(91, ifAnyOf("role:ROLE_ADMIN"))
                        .toEqual(92, ifAnyOf("role:ROLE_USER"))
                        .toEqual(93, ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toEqual(94, ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    @Test
    public void expectToEqual_toPass_multipleAnyOf() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> testService.returnsValue(1);
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                        .toEqual(1, ifAnyOf("role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_VISITOR"))
                        .toEqual(3, ifAnyOf("role:ROLE_SUPER_ADMIN"))
        );
    }

    @Test
    public void expectToEqualAndAssert_toPass() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
            case ROLE_USER:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_SUPER_ADMIN:
            case ROLE_VISITOR:
                call = () -> testService.returnsValue(3);
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                        .toEqual(1, ifAnyOf("role:ROLE_ADMIN", "role:ROLE_USER"))
                        .toAssert(value -> assertThat(value, is(3)), ifAnyOf("role:ROLE_SUPER_ADMIN", "role:ROLE_VISITOR"))
        );
    }

    @Test
    public void expectToEqualAndFail() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> {
                    testService.throwException(new IllegalArgumentException("Msg"));
                    return 0;
                };
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                .toEqual(1, ifAnyOf("role:ROLE_ADMIN", "role:ROLE_USER"))
                .toEqual(3, ifAnyOf("role:ROLE_SUPER_ADMIN"))
                .toFailWithException(
                        IllegalArgumentException.class,
                        ifAnyOf("role:ROLE_VISITOR")
                )
        );
    }

    @Test
    public void expectToEqualAndFail_AssertMessage() throws Throwable {
        ReturnValueCall<Integer> call;
        switch (configuredTest.getConsumer().getRole()) {
            case ROLE_ADMIN:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_USER:
                call = () -> testService.returnsValue(1);
                break;
            case ROLE_SUPER_ADMIN:
                call = () -> testService.returnsValue(3);
                break;
            case ROLE_VISITOR:
                call = () -> {
                    testService.throwException(new IllegalArgumentException("Msg"));
                    return 0;
                };
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(valueOf(call)
                .toEqual(1, ifAnyOf("role:ROLE_ADMIN", "role:ROLE_USER"))
                .toEqual(3, ifAnyOf("role:ROLE_SUPER_ADMIN"))
                .toFailWithException(
                        IllegalArgumentException.class,
                        ifAnyOf("role:ROLE_VISITOR"),
                        thrownException -> assertThat(
                                thrownException.getMessage(),
                                is("Msg")
                        )
                )
        );
    }
    */

}
