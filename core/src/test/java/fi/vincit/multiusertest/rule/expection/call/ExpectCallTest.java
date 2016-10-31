package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.util.AuthorizationFailedException;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Test;

import java.io.IOException;

import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

public class ExpectCallTest {

    void throwDefault() throws Throwable {
        throw new IllegalStateException("Denied");
    }

    void notThrow() {
    }

    void throwIOException() throws IOException {
        throw new IOException("IO Exception");
    }

    void throwCustomException() throws AuthorizationFailedException {
        throw new AuthorizationFailedException("Illegal state");
    }

    @Test
    public void testExpectCallToFail() throws Throwable {
        Expectation call = Expectations.call(this::throwDefault)
                .toFail(ifAnyOf("role:ROLE_USER"));
        call.setExpectedException(IllegalStateException.class);
        call.execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testExpectCallToFail_OtherRole() throws Throwable {
        Expectations.call(this::throwDefault)
                .toFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test
    public void testNoThrow() throws Throwable {
        Expectations.call(this::notThrow)
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test(expected = AssertionError.class)
    public void testNoThrow_ExpectToFail() throws Throwable {
        Expectation call = Expectations.call(this::notThrow)
                .toFail(ifAnyOf("role:ROLE_USER"));
        call.setExpectedException(IllegalStateException.class);
        call.execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = IOException.class)
    public void testThrowsUnexpectedException() throws Throwable {
        Expectation call = Expectations.call(this::throwIOException)
                .toFail(ifAnyOf("role:ROLE_USER"));
        call.setExpectedException(IllegalStateException.class);
        call.execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testNotExpectToFail() throws Throwable {
        Expectations.call(this::notThrow)
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testNotExpectToFail_ButFails() throws Throwable {
        Expectations.call(this::throwDefault)
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testNotExpectToFail_ButFailsWithUnexpected() throws Throwable {
        Expectations.call(this::throwIOException)
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testExpectToFail_WithCustomException_ButDoesntFail() throws Throwable {
        Expectations.call(this::notThrow)
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testExpectToFail_WithCustomException() throws Throwable {
        Expectations.call(this::throwCustomException)
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = RuntimeException.class)
    public void testExpectToFail_WithCustomException_ButFailsWithDefaultException() throws Throwable {
        Expectations.call(this::throwDefault)
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = IOException.class)
    public void testExpectToFail_WithCustomException_ButFailsWithUnexpected() throws Throwable {
        Expectations.call(this::throwIOException)
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectToFail_ButConfiguredIncorrectly() throws Throwable {
        Expectations.call(this::throwIOException)
                .toFailWithException(null, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectToFail_NullUserIdentifier() throws Throwable {
        Expectations.call(this::throwIOException)
                .toFail(null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectNotToFail_NullUserIdentifier() throws Throwable {
        Expectations.call(this::throwIOException)
                .notToFail(null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectToFailWithException_NullUserIdentifier() throws Throwable {
        Expectations.call(this::throwIOException)
                .toFailWithException(Throwable.class, null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testExpectNotToFail_FailsCurrentRole() throws Throwable {
        Expectations.call(this::throwDefault)
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testExpectNotToFail_FailsWithDifferentRole() throws Throwable {
        Expectations.call(this::throwDefault)
                .notToFail(ifAnyOf("role:ROLE_ADMIN"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }
}