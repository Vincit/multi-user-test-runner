package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.rule.expectation.AbstractWhenThen;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ReturnValueWhenThen<VALUE_TYPE> extends AbstractWhenThen<TestValueExpectation<VALUE_TYPE>> {

    private final ReturnValueCall<VALUE_TYPE> valueCall;

    public ReturnValueWhenThen(ReturnValueCall<VALUE_TYPE> valueCall, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier,
                               Authorization authorizationRule, UserRoleIT userRoleIT) {
        super(producerIdentifier, consumerIdentifier, authorizationRule, userRoleIT);
        this.valueCall = valueCall;
    }

    @Override
    public void test(TestValueExpectation<VALUE_TYPE> testExpectation, ConsumerProducerSet consumerProducerSet) throws Throwable {
        try {
            testExpectation.callAndAssertValue(valueCall);
        } catch (Throwable e) {
            testExpectation.handleThrownException(consumerProducerSet, e);
            return;
        }

        testExpectation.handleExceptionNotThrown(consumerProducerSet);
    }

    @Override
    protected TestValueExpectation<VALUE_TYPE> getDefaultExpectation(ConsumerProducerSet consumerProducerSet) {
        return new ReturnValueCallNoExceptionExpectation<>();
    }
}
