package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.rule.expectation2.call.FunctionCallExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation2.call.FunctionCallNoExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation2.value.ReturnValueCallExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation2.value.ReturnValueCallExpectation;
import fi.vincit.multiusertest.rule.expectation2.value.TestValueExpectation;
import fi.vincit.multiusertest.rule.expection.AssertionCall;

public class TestExpectations {

    public static TestExpectation expectException(Class<? extends Throwable> exception) {
        return new FunctionCallExceptionExpectation(exception);
    }

    public static <T extends Throwable> TestExpectation expectException(Class<T> exception, AssertionCall<T> assertion) {
        return new FunctionCallExceptionExpectation<>(exception, assertion);
    }

    public static TestExpectation expectNotToFail() {
        return new FunctionCallNoExceptionExpectation();
    }

    public static <VALUE_TYPE, T extends Throwable> TestValueExpectation<VALUE_TYPE> expectExceptionInsteadOfValue(Class<T> exception) {
        final AssertionCall<T> noopValidation = e -> {};
        return new ReturnValueCallExceptionExpectation<>(exception, noopValidation);
    }

    public static <VALUE_TYPE, T extends Throwable> TestValueExpectation<VALUE_TYPE> expectExceptionInsteadOfValue(Class<T> exception, AssertionCall<T> assertionCall) {
        return new ReturnValueCallExceptionExpectation<>(exception, assertionCall);
    }

    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> assertValue(AssertionCall<VALUE_TYPE> assertion) {
        return new ReturnValueCallExpectation<>(assertion);
    }

    /**
     * Alias for {@link this#assertValue(AssertionCall)}
     * @param assertion Assertion
     * @param <VALUE_TYPE> Type of the response to assert
     * @return
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> assertResponse(AssertionCall<VALUE_TYPE> assertion) {
        return assertValue(assertion);
    }

    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> expectValue(VALUE_TYPE value) {
        return new ReturnValueCallExpectation<>(value);
    }

}
