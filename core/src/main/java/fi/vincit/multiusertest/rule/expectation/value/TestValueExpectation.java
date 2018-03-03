package fi.vincit.multiusertest.rule.expectation.value;

import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;

public interface TestValueExpectation<VALUE_TYPE> extends TestExpectation {

    void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable;
}
