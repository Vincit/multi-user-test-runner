package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.rule.expectation.AbstractWhenThen;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.runner.junit5.Authorization;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ReturnValueWhenThen<VALUE_TYPE> extends AbstractWhenThen<TestValueExpectation<VALUE_TYPE>> {

    private final ReturnValueCall<VALUE_TYPE> valueCall;

    public ReturnValueWhenThen(ReturnValueCall<VALUE_TYPE> valueCall, UserIdentifier userIdentifier, Authorization authorizationRule) {
        super(userIdentifier, authorizationRule);
        this.valueCall = valueCall;
    }

    @Override
    public void test(TestValueExpectation<VALUE_TYPE> testExpectation, UserIdentifier userIdentifier) throws Throwable {
        try {
            testExpectation.callAndAssertValue(valueCall);
        } catch (Throwable e) {
            testExpectation.handleThrownException(userIdentifier, e);
            return;
        }

        testExpectation.handleExceptionNotThrown(userIdentifier);
    }

    @Override
    protected TestValueExpectation<VALUE_TYPE> getDefaultExpectation(UserIdentifier userIdentifier) {
        return new ReturnValueCallNoExceptionExpectation<>();
    }
}
