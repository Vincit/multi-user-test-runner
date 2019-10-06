package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.rule.expectation.AbstractWhenThen;
import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;

public class FunctionCallWhenThen extends AbstractWhenThen<TestExpectation> {

    private final FunctionCall functionCall;

    public FunctionCallWhenThen(FunctionCall function, UserIdentifier identifier, Authorization authorizationRule,
                                UserRoleIT userRoleIT) {
        super(identifier, authorizationRule, userRoleIT);
        this.functionCall = function;
    }

    @Override
    public void test(TestExpectation testExpectation, UserIdentifier userIdentifier) throws Throwable {
        try {
            functionCall.call();
        } catch (Throwable e) {
            testExpectation.handleThrownException(userIdentifier, e);
            return;
        }
        testExpectation.handleExceptionNotThrown(userIdentifier);
    }

    @Override
    protected TestExpectation getDefaultExpectation(UserIdentifier userIdentifier) {
        return new FunctionCallNoExceptionExpectation();
    }

}