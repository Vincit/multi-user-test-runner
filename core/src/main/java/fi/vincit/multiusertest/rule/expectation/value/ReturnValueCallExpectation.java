package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation.AssertionCall;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReturnValueCallExpectation<VALUE_TYPE> implements TestValueExpectation<VALUE_TYPE> {

    private final VALUE_TYPE value;
    private final AssertionCall<VALUE_TYPE> assertionCall;

    public ReturnValueCallExpectation(VALUE_TYPE value) {
        this.value = Objects.requireNonNull(value);
        this.assertionCall = null;
    }

    public ReturnValueCallExpectation(AssertionCall<VALUE_TYPE> assertionCall) {
        this.value = null;
        this.assertionCall = Objects.requireNonNull(assertionCall);
    }

    @Override
    public void handleExceptionNotThrown(ConsumerProducerSet consumerProducerSet) {
        // NOOP?
    }

    @Override
    public void handleThrownException(ConsumerProducerSet consumerProducerSet, Throwable thrownException)  throws Throwable {
        throw CallFailedError.expectCallNotToFail(consumerProducerSet, thrownException);
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        VALUE_TYPE returnValue = valueCall.call();

        if (assertionCall != null) {
            assertionCall.call(returnValue);
        } else {
            assertThat(returnValue, is(value));
        }
    }

    @Override
    public String toString() {
        final String stringValue;
        if (value != null) {
            stringValue = value.toString();
        } else {
            stringValue = "null";
        }
        return "Expect value: " + stringValue;
    }
}
