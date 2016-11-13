package fi.vincit.multiusertest.rule.expectation2.call;

import fi.vincit.multiusertest.rule.expectation2.TestExpectation;
import fi.vincit.multiusertest.util.UserIdentifier;

public class FunctionCallNoExceptionExpectation implements TestExpectation {

    public FunctionCallNoExceptionExpectation() {
    }

    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        // NOOP
    }

    public void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException)  throws Throwable {
        throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), thrownException);
    }

}
