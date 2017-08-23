package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ReturnValueCallNoExceptionExpectation<VALUE_TYPE> implements TestValueExpectation<VALUE_TYPE> {


    public ReturnValueCallNoExceptionExpectation() {
    }

    @Override
    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        // NOOP
    }

    @Override
    public void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException)  throws Throwable {
        throw CallFailedError.expectCallNotToFail(userIdentifier, thrownException);
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        valueCall.call();
    }

}
