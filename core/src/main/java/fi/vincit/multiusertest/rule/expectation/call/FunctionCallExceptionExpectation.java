package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation.AssertionCall;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;

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

    public void handleExceptionNotThrown(ConsumerProducerSet consumerProducerSet) {
        throw CallFailedError.expectedCallToFail(consumerProducerSet, defaultExpectedException);
    }

    public void handleThrownException(ConsumerProducerSet consumerProducerSet, Throwable thrownException)  throws Throwable {
        if (!defaultExpectedException.isInstance(thrownException)) {
            throw CallFailedError.unexpectedException(consumerProducerSet, defaultExpectedException, thrownException);
        }

        if (assertion.isPresent()) {
            assertion.get().call((T) thrownException);
        }
    }

    @Override
    public String toString() {
        return "Expect exception: " + defaultExpectedException.getSimpleName();
    }
}
