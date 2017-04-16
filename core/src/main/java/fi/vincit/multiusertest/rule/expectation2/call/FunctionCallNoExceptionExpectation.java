package fi.vincit.multiusertest.rule.expectation2.call;

import fi.vincit.multiusertest.exception.CallFailedError;
import fi.vincit.multiusertest.rule.expectation2.TestExpectation;
import fi.vincit.multiusertest.util.UserIdentifier;

public class FunctionCallNoExceptionExpectation implements TestExpectation {

    public FunctionCallNoExceptionExpectation() {
    }

    @Override
    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        // NOOP
    }

    @Override
    public void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException) {
        throw CallFailedError.expectCallNotToFail(userIdentifier, thrownException);
    }

}
