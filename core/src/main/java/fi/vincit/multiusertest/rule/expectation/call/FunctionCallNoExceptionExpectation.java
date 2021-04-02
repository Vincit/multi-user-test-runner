package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;

public class FunctionCallNoExceptionExpectation implements TestExpectation {

    public FunctionCallNoExceptionExpectation() {
    }

    @Override
    public void handleExceptionNotThrown(ConsumerProducerSet consumerProducerSet) {
        // NOOP
    }

    @Override
    public void handleThrownException(ConsumerProducerSet consumerProducerSet, Throwable thrownException) {
        throw CallFailedError.expectCallNotToFail(consumerProducerSet, thrownException);
    }

    @Override
    public String toString() {
        return "No exception expected. (No value returned)";
    }
}
