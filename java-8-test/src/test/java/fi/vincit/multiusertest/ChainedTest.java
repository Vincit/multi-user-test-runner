package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.expection.call.ExpectCall;
import fi.vincit.multiusertest.rule.expection.value.ExpectValueOf;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;

@RunWithUsers(producers = {"role:ROLE_SUPER_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", },
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_VISITOR"})
@RunWith(MultiUserTestRunner.class)
public class ChainedTest extends ConfiguredTest {

    private TestService testService = new TestService();

    @Test
    public void expectAssert_toPass() throws Throwable {
        ExpectValueOf<Integer> expectValueOf;
        switch (getUser().getRole()) {
            case ROLE_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_USER:
                expectValueOf = valueOf(() -> testService.returnsValue(2));
                break;
            case ROLE_SUPER_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            case ROLE_VISITOR:
                expectValueOf = valueOf(() -> testService.returnsValue(4));
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
                        .toAssert((value) -> assertThat(value, is(1)), ifAnyOf("role:ROLE_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(2)), ifAnyOf("role:ROLE_USER"))
                        .toAssert((value) -> assertThat(value, is(3)), ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(4)), ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectAssert_toFail() throws Throwable {
        ExpectValueOf<Integer> expectValueOf;
        switch (getUser().getRole()) {
            case ROLE_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_USER:
                expectValueOf = valueOf(() -> testService.returnsValue(2));
                break;
            case ROLE_SUPER_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            case ROLE_VISITOR:
                expectValueOf = valueOf(() -> testService.returnsValue(4));
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
                        .toAssert((value) -> assertThat(value, is(91)), ifAnyOf("role:ROLE_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(92)), ifAnyOf("role:ROLE_USER"))
                        .toAssert((value) -> assertThat(value, is(93)), ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toAssert((value) -> assertThat(value, is(94)), ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    public void expectAssert_toFailWithException_toPass() throws Throwable {
        ExpectCall expectValueOf = call(testService::throwAccessDenied);

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
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

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
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
        ExpectValueOf<Integer> expectValueOf;
        switch (getUser().getRole()) {
            case ROLE_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_USER:
                expectValueOf = valueOf(() -> testService.returnsValue(2));
                break;
            case ROLE_SUPER_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            case ROLE_VISITOR:
                expectValueOf = valueOf(() -> testService.returnsValue(4));
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
                        .toEqual(1, ifAnyOf("role:ROLE_ADMIN"))
                        .toEqual(2, ifAnyOf("role:ROLE_USER"))
                        .toEqual(3, ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toEqual(4, ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    @Test(expected = AssertionError.class)
    public void expectToEqual_toFail() throws Throwable {
        ExpectValueOf<Integer> expectValueOf;
        switch (getUser().getRole()) {
            case ROLE_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_USER:
                expectValueOf = valueOf(() -> testService.returnsValue(2));
                break;
            case ROLE_SUPER_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            case ROLE_VISITOR:
                expectValueOf = valueOf(() -> testService.returnsValue(4));
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
                        .toEqual(91, ifAnyOf("role:ROLE_ADMIN"))
                        .toEqual(92, ifAnyOf("role:ROLE_USER"))
                        .toEqual(93, ifAnyOf("role:ROLE_SUPER_ADMIN"))
                        .toEqual(94, ifAnyOf("role:ROLE_VISITOR"))
        );
    }

    @Test
    public void expectToEqual_toPass_multipleAnyOf() throws Throwable {
        ExpectValueOf<Integer> expectValueOf;
        switch (getUser().getRole()) {
            case ROLE_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_USER:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_SUPER_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            case ROLE_VISITOR:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
                        .toEqual(1, ifAnyOf("role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_VISITOR"))
                        .toEqual(3, ifAnyOf("role:ROLE_SUPER_ADMIN"))
        );
    }

    @Test
    public void expectToEqualAndAssert_toPass() throws Throwable {
        ExpectValueOf<Integer> expectValueOf;
        switch (getUser().getRole()) {
            case ROLE_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_USER:
                expectValueOf = valueOf(() -> testService.returnsValue(1));
                break;
            case ROLE_SUPER_ADMIN:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            case ROLE_VISITOR:
                expectValueOf = valueOf(() -> testService.returnsValue(3));
                break;
            default:
                throw new IllegalArgumentException("Missing ROLE definition");
        }

        logInAs(LoginRole.USER);
        authorization().expect(expectValueOf
                        .toEqual(1, ifAnyOf("role:ROLE_ADMIN", "role:ROLE_USER"))
                        .toAssert(value -> assertThat(value, is(3)), ifAnyOf("role:ROLE_SUPER_ADMIN", "role:ROLE_VISITOR"))
        );
    }
}
