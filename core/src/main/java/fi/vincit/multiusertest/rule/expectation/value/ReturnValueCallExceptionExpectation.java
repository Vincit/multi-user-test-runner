package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation.AssertionCall;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;

import java.util.Optional;

public class ReturnValueCallExceptionExpectation<VALUE_TYPE, EXCEPTION extends Throwable> implements TestValueExpectation<VALUE_TYPE> {

    private final Optional<AssertionCall<EXCEPTION>> exceptionAssertionCall;
    private final Class<? extends Throwable> expectedException;

    public ReturnValueCallExceptionExpectation(Class<EXCEPTION> expectedException) {
        this(expectedException, null);
    }

    public ReturnValueCallExceptionExpectation(Class<EXCEPTION> expectedException, AssertionCall<EXCEPTION> assertionCall) {
        this.exceptionAssertionCall = Optional.ofNullable(assertionCall);
        this.expectedException = expectedException;
    }

    public void handleExceptionNotThrown(ConsumerProducerSet consumerProducerSet) {
        throw CallFailedError.expectedCallToFail(consumerProducerSet, expectedException);
    }

    public void handleThrownException(ConsumerProducerSet consumerProducerSet, Throwable thrownException)  throws Throwable {
        if (!expectedException.isInstance(thrownException)) {
            throw CallFailedError.unexpectedException(
                    consumerProducerSet,
                    expectedException,
                    thrownException
            );
        }

        if (exceptionAssertionCall.isPresent()) {
            exceptionAssertionCall.get().call((EXCEPTION) thrownException);
        }
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        valueCall.call();
    }

    @Override
    public String toString() {
        return "Expect exception and assert it: " + expectedException.getSimpleName();
    }
}
