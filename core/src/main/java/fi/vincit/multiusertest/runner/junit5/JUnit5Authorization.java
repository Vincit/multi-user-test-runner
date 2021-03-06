package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.rule.expectation.WhenThen;
import fi.vincit.multiusertest.rule.expectation.call.FunctionCallWhenThen;
import fi.vincit.multiusertest.rule.expectation.value.ReturnValueWhenThen;
import fi.vincit.multiusertest.rule.expectation.value.TestValueExpectation;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;

public class JUnit5Authorization implements Authorization {

    private UserIdentifier userIdentifier;
    private UserIdentifier producerIdentifier;
    private UserRoleIT userRoleIT;
    
    @Override
    public void setUserRoleIT(UserRoleIT userRoleIT) {
        this.userRoleIT = userRoleIT;
    }

    @Override
    public void setRole(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        this.producerIdentifier = new UserIdentifier(producerIdentifier.getType(), producerIdentifier.getIdentifier());
        this.userIdentifier = new UserIdentifier(consumerIdentifier.getType(), consumerIdentifier.getIdentifier());
    }

    @Override
    public WhenThen<TestExpectation> given(FunctionCall functionCall) {
        return new FunctionCallWhenThen(functionCall, null, userIdentifier, this, userRoleIT);
    }

    @Override
    public <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> given(ReturnValueCall<VALUE_TYPE> returnValueCall) {
        return new ReturnValueWhenThen<>(
                returnValueCall,
                null,
                userIdentifier,
                this, 
                userRoleIT
        );
    }

    @Override
    public void markExpectationConstructed() {
        // NOOP
    }

}
