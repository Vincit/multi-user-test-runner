package fi.vincit.multiusertest.rule.expection.call;

import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

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

    void throwIllegalStateException() {
        throw new IllegalStateException("Illegal state");
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

    @Test(expected = IOException.class)
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

    @Test(expected = AssertionError.class)
    public void testExpectToFail_WithCustomException_ButDoesntFail() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                notThrow();
            }
        })
                .toFailWithException(IllegalStateException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testExpectToFail_WithCustomException() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIllegalStateException();
            }
        })
                .toFailWithException(IllegalStateException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AccessDeniedException.class)
    public void testExpectToFail_WithCustomException_ButFailsWithDefaultException() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwAccessDenied();
            }
        })
                .toFailWithException(IllegalStateException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = IOException.class)
    public void testExpectToFail_WithCustomException_ButFailsWithUnexpected() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .toFailWithException(IllegalStateException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectToFail_ButConfiguredIncorrectly() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .toFailWithException(null, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectToFail_NullUserIdentifier() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .toFail(null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectNotToFail_NullUserIdentifier() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .notToFail(null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = NullPointerException.class)
    public void testExpectToFailWithException_NullUserIdentifier() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .toFailWithException(Throwable.class, null)
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }
}