package fi.vincit.multiusertest.rule.expection.value;

import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ExpectValueOfTest {

    @Test
    public void testReturnValueEquals() throws Throwable{
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        }).toEqual(1, ifAnyOf("role:ROLE_USER")).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testReturnValueEquals_MultipleIdentifiers() throws Throwable {
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        })
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .toEqual(2, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testReturnValueEquals_MultipleIdentifiers_OneFails() throws Throwable {
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        })
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .toEqual(2, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test
    public void testAssertion_Passes() throws Throwable {
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        })
                .toAssert(new AssertionCall<Integer>() {
                    @Override
                    public void call(Integer value) {
                        assertThat(value, is(1));
                    }
                }, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testAssertion_Fails() throws Throwable {
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        })
                .toAssert(new AssertionCall<Integer>() {
                    @Override
                    public void call(Integer value) {
                        assertThat(value, is(2));
                    }
                }, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testAssertion_NoAssertionFound() throws Throwable {
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        })
                .toAssert(new AssertionCall<Integer>() {
                    @Override
                    public void call(Integer value) {
                        assertThat(value, is(2));
                    }
                }, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }



}