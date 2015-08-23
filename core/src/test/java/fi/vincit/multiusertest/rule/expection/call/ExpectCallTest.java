package fi.vincit.multiusertest.rule.expection.call;

import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import java.io.IOException;

import org.junit.Test;

import fi.vincit.multiusertest.rule.expection.Expectations;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.util.AuthorizationFailedException;
import fi.vincit.multiusertest.util.UserIdentifier;

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
        ExpectCall call = Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwDefault();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"));
        call.setExpectedException(IllegalStateException.class);
        call.execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = AssertionError.class)
    public void testExpectCallToFail_OtherRole() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwDefault();
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
        ExpectCall call = Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                notThrow();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"));
        call.setExpectedException(IllegalStateException.class);
        call.execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = IOException.class)
    public void testThrowsUnexpectedException() throws Throwable {
        ExpectCall call = Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwIOException();
            }
        })
                .toFail(ifAnyOf("role:ROLE_USER"));
        call.setExpectedException(IllegalStateException.class);
        call.execute(UserIdentifier.parse("role:ROLE_USER"));
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
                throwDefault();
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
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test
    public void testExpectToFail_WithCustomException() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwCustomException();
            }
        })
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
                .execute(UserIdentifier.parse("role:ROLE_USER"));
    }

    @Test(expected = RuntimeException.class)
    public void testExpectToFail_WithCustomException_ButFailsWithDefaultException() throws Throwable {
        Expectations.call(new FunctionCall() {
            @Override
            public void call() throws Throwable {
                throwDefault();
            }
        })
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
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
                .toFailWithException(AuthorizationFailedException.class, ifAnyOf("role:ROLE_USER"))
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