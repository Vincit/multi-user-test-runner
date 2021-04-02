package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;

public class ReturnValueCallNoExceptionExpectation<VALUE_TYPE> implements TestValueExpectation<VALUE_TYPE> {


    public ReturnValueCallNoExceptionExpectation() {
    }

    @Override
    public void handleExceptionNotThrown(ConsumerProducerSet consumerProducerSet) {
        // NOOP
    }

    @Override
    public void handleThrownException(ConsumerProducerSet consumerProducerSet, Throwable thrownException)  throws Throwable {
        throw CallFailedError.expectCallNotToFail(consumerProducerSet, thrownException);
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        valueCall.call();
    }

    @Override
    public String toString() {
        return "No exception expected (Value returned)";
    }
}
