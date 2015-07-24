package fi.vincit.multiusertest.rule;


import static fi.vincit.multiusertest.rule.Authentication.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fi.vincit.multiusertest.util.UserIdentifier;

public class ExpectValueOfTest {

    @Test
    public void testReturnValueEquals() {
        Expectation.valueOf(new ExpectValueOfCallback<Integer>() {
            @Override
            public Integer doIt() {
                return 1;
            }
        }).toEqual(1, ifAnyOf("role:ROLE_USER")).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testReturnValueEquals_MultipleIdentifiers() {
        Expectation.valueOf(new ExpectValueOfCallback<Integer>() {
            @Override
            public Integer doIt() {
                return 1;
            }
        })
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .toEqual(2, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testReturnValueEquals_MultipleIdentifiers_OneFails() {
        Expectation.valueOf(new ExpectValueOfCallback<Integer>() {
            @Override
            public Integer doIt() {
                return 1;
            }
        })
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .toEqual(2, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test
    public void testAssertion_Passes() {
        Expectation.valueOf(new ExpectValueOfCallback<Integer>() {
            @Override
            public Integer doIt() {
                return 1;
            }
        })
                .toAssert(new Callback<Integer>() {
                    @Override
                    public void doIt(Integer value) {
                        assertThat(value, is(1));
                    }
                }, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testAssertion_Fails() {
        Expectation.valueOf(new ExpectValueOfCallback<Integer>() {
            @Override
            public Integer doIt() {
                return 1;
            }
        })
                .toAssert(new Callback<Integer>() {
                    @Override
                    public void doIt(Integer value) {
                        assertThat(value, is(2));
                    }
                }, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testAssertion_NoAssertionFound() {
        Expectation.valueOf(new ExpectValueOfCallback<Integer>() {
            @Override
            public Integer doIt() {
                return 1;
            }
        })
                .toAssert(new Callback<Integer>() {
                    @Override
                    public void doIt(Integer value) {
                        assertThat(value, is(2));
                    }
                }, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }



}