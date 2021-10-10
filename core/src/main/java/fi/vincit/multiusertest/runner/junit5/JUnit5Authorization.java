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
import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.HashSet;
import java.util.Set;

public class JUnit5Authorization implements Authorization {

    private final Set<UserIdentifier> allowedIdentifiers = new HashSet<>();
    private UserIdentifier userIdentifier;
    private UserIdentifier producerIdentifier;
    private UserRoleIT userRoleIT;
    private FocusType focusType;

    @Override
    public void setAllowedIdentifiers(Set<UserIdentifier> allowedIdentifiers) {
        this.allowedIdentifiers.clear();
        this.allowedIdentifiers.addAll(allowedIdentifiers);
    }

    @Override
    public void setFocusType(FocusType focusType) {
        this.focusType = focusType;
    }

    @Override
    public void setUserRoleIT(UserRoleIT userRoleIT) {
        this.userRoleIT = userRoleIT;
    }

    @Override
    public void setRole(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        this.producerIdentifier = new UserIdentifier(producerIdentifier.getType(), producerIdentifier.getIdentifier(), producerIdentifier.getFocusMode());
        this.userIdentifier = new UserIdentifier(consumerIdentifier.getType(), consumerIdentifier.getIdentifier(), consumerIdentifier.getFocusMode());
    }

    @Override
    public WhenThen<TestExpectation> given(FunctionCall functionCall) {
        return new FunctionCallWhenThen(
                functionCall,
                producerIdentifier,
                userIdentifier,
                this,
                userRoleIT,
                allowedIdentifiers,
                focusType
        );
    }

    @Override
    public <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> given(ReturnValueCall<VALUE_TYPE> returnValueCall) {
        return new ReturnValueWhenThen<>(
                returnValueCall,
                producerIdentifier,
                userIdentifier,
                this, 
                userRoleIT,
                allowedIdentifiers,
                focusType
        );
    }

    @Override
    public void markExpectationConstructed() {
        // NOOP
    }

    @Override
    public void markErrorOccurred() {
        // NOOP
    }
}
