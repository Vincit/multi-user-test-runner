package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.rule.expectation.WhenThen;
import fi.vincit.multiusertest.rule.expectation.value.TestValueExpectation;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Set;

public interface Authorization {

    WhenThen<TestExpectation> given(FunctionCall functionCall);

    <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> given(ReturnValueCall<VALUE_TYPE> returnValueCall);

    void markExpectationConstructed();

    void markErrorOccurred();

    void setRole(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier);
    
    void setUserRoleIT(UserRoleIT userRoleIT);

    void setAllowedIdentifiers(Set<UserIdentifier> allowedIdentifiers);

    void setFocusType(FocusType focusType);
}
