package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.rule.expectation.WhenThen;
import fi.vincit.multiusertest.rule.expectation.call.FunctionCallWhenThen;
import fi.vincit.multiusertest.rule.expectation.value.ReturnValueWhenThen;
import fi.vincit.multiusertest.rule.expectation.value.TestValueExpectation;
import fi.vincit.multiusertest.util.UserIdentifier;

public class JUnit5Authorization implements Authorization {

    private UserIdentifier userIdentifier;
    private boolean expectationConstructionFinished = false;

    @Override
    public void setRole(UserIdentifier identifier) {
        this.userIdentifier = new UserIdentifier(identifier.getType(), identifier.getIdentifier());
    }

    @Override
    public WhenThen<TestExpectation> testCall(FunctionCall functionCall) {
        expectationConstructionFinished = true;
        return new FunctionCallWhenThen(functionCall, userIdentifier, this);
    }

    @Override
    public <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> testCall(ReturnValueCall<VALUE_TYPE> returnValueCall) {
        expectationConstructionFinished = true;
        return new ReturnValueWhenThen<>(
                returnValueCall,
                userIdentifier,
                this
        );
    }

    @Override
    public void markExpectationConstructed() {
        expectationConstructionFinished = true;
    }
}
