package fi.vincit.multiusertest.rule.expectation2.call;

import fi.vincit.multiusertest.rule.expectation2.TestExpectation;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Optional;

public class FunctionCallExceptionExpectation<T extends Throwable> implements TestExpectation {

    private Class<? extends Throwable> defaultExpectedException;
    private Optional<AssertionCall<T>> assertion;

    public FunctionCallExceptionExpectation(Class<T> defaultExpectedException) {
        this(defaultExpectedException, null);
    }

    public FunctionCallExceptionExpectation(Class<T> exception, AssertionCall<T> assertion) {
        this.defaultExpectedException = exception;
        this.assertion = Optional.ofNullable(assertion);
    }

    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        throw new AssertionError("Expected to fail with exception " + defaultExpectedException.getName());
    }

    public void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException)  throws Throwable {
        if (!defaultExpectedException.isInstance(thrownException)) {
            String message = String.format("Unexpected exception thrown. Expected <%s> but was <%s>",
                    defaultExpectedException.getSimpleName(),
                    thrownException.getClass().getSimpleName()
            );
            throw new AssertionError(message, thrownException);
        }

        if (assertion.isPresent()) {
            assertion.get().call((T) thrownException);
        }
    }

}
