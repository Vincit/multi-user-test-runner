package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.rule.expectation2.TestExpectation;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;

public interface TestValueExpectation<VALUE_TYPE> extends TestExpectation {

    void callAndAssertValue(ReturnValueCall<VALUE_TYPE> valueCall) throws Throwable;
}
