package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.UserIdentifier;

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

    public void handleExceptionNotThrown(UserIdentifier userIdentifier) {
        throw new AssertionError("Expected to fail with exception " + expectedException.getName());
    }

    public void handleThrownException(UserIdentifier userIdentifier, Throwable e)  throws Throwable {
        if (!expectedException.isInstance(e)) {
            throw e;
        }

        if (exceptionAssertionCall.isPresent()) {
            exceptionAssertionCall.get().call((EXCEPTION)e);
        }
    }

    @Override
    public void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable {
        valueCall.call();
    }

}
