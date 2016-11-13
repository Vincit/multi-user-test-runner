package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReturnValueCallExpectation<VALUE_TYPE> implements TestValueExpectation<VALUE_TYPE> {

    private Optional<VALUE_TYPE> value;
    private Optional<AssertionCall<VALUE_TYPE>> assertionCall;

    public ReturnValueCallExpectation(VALUE_TYPE value) {
        this.value = Optional.of(value);
        this.assertionCall = Optional.empty();
    }

    public ReturnValueCallExpectation(AssertionCall<VALUE_TYPE> assertionCall) {
        this.value = Optional.empty();
        this.assertionCall = Optional.of(assertionCall);
    }

    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        // NOOP?
    }

    public void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException)  throws Throwable {
        throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), thrownException);
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        Optional<Throwable> exceptionOnAssert = Optional.empty();
        VALUE_TYPE returnValue = valueCall.call();

        if (assertionCall.isPresent()) {
            try {
                assertionCall.get().call(returnValue);
            } catch (Throwable t) {
                exceptionOnAssert = Optional.of(t);
            }
        } else {
            assertThat(returnValue, is(value.orElse(null)));
        }

        if (exceptionOnAssert.isPresent()) {
            throw exceptionOnAssert.get();
        }
    }

}
