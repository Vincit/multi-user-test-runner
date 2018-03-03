package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation.AssertionCall;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
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

    @Override
    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        // NOOP?
    }

    @Override
    public void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException)  throws Throwable {
        throw CallFailedError.expectCallNotToFail(userIdentifier, thrownException);
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        VALUE_TYPE returnValue = valueCall.call();

        if (assertionCall.isPresent()) {
            try {
                assertionCall.get().call(returnValue);
            } catch (Throwable throwable) {
                throw throwable;
            }
        } else {
            assertThat(returnValue, is(value.orElse(null)));
        }
    }

}
