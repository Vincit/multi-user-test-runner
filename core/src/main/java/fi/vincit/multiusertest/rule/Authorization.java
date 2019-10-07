package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.rule.expectation.WhenThen;
import fi.vincit.multiusertest.rule.expectation.value.TestValueExpectation;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;

public interface Authorization {

    WhenThen<TestExpectation> given(FunctionCall functionCall);

    <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> given(ReturnValueCall<VALUE_TYPE> returnValueCall);

    void markExpectationConstructed();

    void setRole(UserIdentifier identifier);
    
    void setUserRoleIT(UserRoleIT userRoleIT);
}
