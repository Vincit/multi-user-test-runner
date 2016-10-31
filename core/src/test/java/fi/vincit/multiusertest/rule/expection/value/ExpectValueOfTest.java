package fi.vincit.multiusertest.rule.expection.value;

import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Test;

import java.io.IOException;

import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ExpectValueOfTest {

    @Test
    public void testReturnValueEquals() throws Throwable{
        Expectations.valueOf(() -> 1)
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testReturnValueEqualsNull() throws Throwable{
        Expectations.valueOf(() -> null)
                .toEqual(null, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testReturnValueEqualsNull_FailsWhenNonNullReturned() throws Throwable{
        Expectations.valueOf(() -> 1)
                .toEqual(null, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testReturnValueEquals_FailsWhenNullReturned() throws Throwable{
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() {
                return null;
            }
        }).toEqual(1, ifAnyOf("role:ROLE_USER")).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testReturnValueEquals_MultipleIdentifiers() throws Throwable {
        Expectations.valueOf(() -> 1)
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .toEqual(2, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testReturnValueEquals_MultipleIdentifiers_OneFails() throws Throwable {
        Expectations.valueOf(() -> 1)
                .toEqual(1, ifAnyOf("role:ROLE_USER"))
                .toEqual(2, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test
    public void testAssertion_Passes() throws Throwable {
        Expectations.valueOf(() -> 1)
                .toAssert(value ->
                        assertThat(value, is(1)), ifAnyOf("role:ROLE_USER")
                ).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testAssertion_Fails() throws Throwable {
        Expectations.valueOf(() -> 1)
                .toAssert(value ->
                        assertThat(value, is(2)), ifAnyOf("role:ROLE_USER")
                ).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testAssertion_NoAssertionFound() throws Throwable {
        Expectations.valueOf(() -> 1)
                .toAssert(value ->
                        assertThat(value, is(2)), ifAnyOf("role:ROLE_USER")
                ).execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test(expected = NullPointerException.class)
    public void testReturnValue_CallIsNull() throws Throwable{
        Expectations.valueOf(() -> 1).toAssert(null, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testReturnValue_UserIdentifiersNull() throws Throwable{
        Expectations.valueOf(() -> 1)
                .toAssert(value -> {}, null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testExceptionIsThrownFromToEquals() throws Throwable{
        Expectations.valueOf(new ReturnValueCall<Integer>() {
            @Override
            public Integer call() throws IOException {
                throw new IOException();
            }
        }).toEqual(1, ifAnyOf("role:ROLE_USER")).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = IOException.class)
    public void testExceptionIsThrownFromToAssert() throws Throwable{
        Expectations.valueOf(() -> 1)
                .toAssert(value -> {throw new IOException();},
                        ifAnyOf("role:ROLE_USER")
                ).execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testExceptionIsThrownFromToEqualsAndFails() throws Throwable{
        Expectations.valueOf(() -> {throw new IOException();})
                .toFailWithException(IOException.class, ifAnyOf("role:ROLE_USER"))
                .toEqual(1, ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }


}