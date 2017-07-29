package fi.vincit.multiusertest.exception;

import fi.vincit.multiusertest.util.UserIdentifier;

public class CallFailedException extends AssertionError {

    public static AssertionError expectCallNotToFail(UserIdentifier userIdentifier, Throwable exception) {
        return new CallFailedException(String.format(
                "Assertion failed with role <%s>: %s",
                userIdentifier,
                exception
        ), exception);
    }

    public static AssertionError expectedCallToFail(UserIdentifier userIdentifier, Class<? extends Throwable> expected) {
        return new CallFailedException(String.format(
                "Expected assertion to fail with role <%s> with exception %s. " +
                "No exception was thrown.",
                userIdentifier.toString(),
                expected.getName()
        ));
    }

    public static AssertionError unexpectedException(UserIdentifier userIdentifier, Class<? extends Throwable> expected, Throwable thrown) {
        return new CallFailedException(String.format(
                "Unexpected exception thrown with role <%s>: " +
                "Expected <%s> but was <%s>: " +
                "%s",
                userIdentifier,
                expected.getSimpleName(),
                thrown.getClass().getSimpleName(),
                thrown.getMessage()
        ), thrown);
    }

    private CallFailedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    private CallFailedException(Object o) {
        super(o);
    }
}
