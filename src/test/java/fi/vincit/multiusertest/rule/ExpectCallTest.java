package fi.vincit.multiusertest.rule;

import static fi.vincit.multiusertest.rule.Authentication.ifAnyOf;

import java.io.IOException;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ExpectCallTest {

    void throwAccessDenied() throws AccessDeniedException {
        throw new AccessDeniedException("Denied");
    }

    void notThrow() throws AccessDeniedException {
    }

    void throwIOException() throws IOException {
        throw new IOException("IO Exception");
    }

    @Test
    public void testExpectCallToFail() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwAccessDenied();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testExpectCallToFail_OtherRole() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwAccessDenied();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test
    public void testNoThrow() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                notThrow();
            }
        }).execute(UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test(expected = AssertionError.class)
    public void testNoThrow_ExpectToFail() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                notThrow();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testThrowsUnexpectedException() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testNotExpectToFail() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                notThrow();
            }
        })
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testNotExpectToFail_ButFails() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwAccessDenied();
            }
        })
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testNotExpectToFail_ButFailsWithUnexpected() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .notToFail(ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }
}