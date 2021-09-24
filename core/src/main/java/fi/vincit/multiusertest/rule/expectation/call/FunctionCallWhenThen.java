package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.rule.expectation.AbstractWhenThen;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Set;

public class FunctionCallWhenThen extends AbstractWhenThen<TestExpectation> {

    private final FunctionCall functionCall;

    public FunctionCallWhenThen(FunctionCall function, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier,
                                Authorization authorizationRule, UserRoleIT userRoleIT, Set<UserIdentifier> allowedIdentifiers,
                                FocusType focusType) {
        super(producerIdentifier, consumerIdentifier, authorizationRule, userRoleIT, allowedIdentifiers, focusType);
        this.functionCall = function;
    }

    @Override
    public void test(TestExpectation testExpectation, ConsumerProducerSet consumerProducerSet) throws Throwable {
        try {
            functionCall.call();
        } catch (Throwable e) {
            testExpectation.handleThrownException(consumerProducerSet, e);
            return;
        }
        testExpectation.handleExceptionNotThrown(consumerProducerSet);
    }

    @Override
    protected TestExpectation getDefaultExpectation(ConsumerProducerSet consumerProducerSet) {
        return new FunctionCallNoExceptionExpectation();
    }

}
