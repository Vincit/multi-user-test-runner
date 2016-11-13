package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.rule.expectation2.call.FunctionCallExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation2.call.FunctionCallNoExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation2.value.ReturnValueCallExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation2.value.ReturnValueCallExpectation;
import fi.vincit.multiusertest.rule.expectation2.value.TestValueExpectation;
import fi.vincit.multiusertest.rule.expection.AssertionCall;

/**
 * Expectation for expectation API 2. Expectation take more BDD like
 * approach constructing assertions and uses less nested calls.
 * @since 0.5
 */
public class TestExpectations {

    /**
     * Expect that an exception of the given type should be thrown
     * @param exception Exception expected
     * @return
     * @since 0.5
     */
    public static TestExpectation expectException(Class<? extends Throwable> exception) {
        return new FunctionCallExceptionExpectation(exception);
    }

    /**
     * Expect that an exception of the given type should be thrown and be
     * asserted with the given {@link AssertionCall}
     * @param exception Exception expected
     * @param assertion Custom assertion
     * @param <T> Type of the exception
     * @return
     * @since 0.5
     */
    public static <T extends Throwable> TestExpectation expectException(Class<T> exception, AssertionCall<T> assertion) {
        return new FunctionCallExceptionExpectation<>(exception, assertion);
    }

    /**
     * Explicitly mark that a call shouldn't fail.
     * @return
     * @since 0.5
     */
    public static TestExpectation expectNotToFail() {
        return new FunctionCallNoExceptionExpectation();
    }

    /**
     * Exception is expected instead of call under test returning a value
     * @param exception Exception expected
     * @param <VALUE_TYPE> Type of the return value of the call under test
     * @param <T> Type of the exception
     * @return
     * @since 0.5
     */
    public static <VALUE_TYPE, T extends Throwable> TestValueExpectation<VALUE_TYPE> expectExceptionInsteadOfValue(Class<T> exception) {
        final AssertionCall<T> noopValidation = e -> {};
        return new ReturnValueCallExceptionExpectation<>(exception, noopValidation);
    }

    /**
     * Exception is expected instead of call under test returning a value and exception
     * is asserted with the given {@link AssertionCall}
     * @param exception Exception expected
     * @param assertionCall Custom assertion
     * @param <VALUE_TYPE> Type of the return value of the call under test
     * @param <T> Type of the exception
     * @return
     * @since 0.5
     */
    public static <VALUE_TYPE, T extends Throwable> TestValueExpectation<VALUE_TYPE> expectExceptionInsteadOfValue(Class<T> exception, AssertionCall<T> assertionCall) {
        return new ReturnValueCallExceptionExpectation<>(exception, assertionCall);
    }

    /**
     * Assert returned value with the given {@link AssertionCall}
     * @param assertion Custom assertion
     * @param <VALUE_TYPE> Type of the value to be asserted
     * @return
     * @since 0.5
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> assertValue(AssertionCall<VALUE_TYPE> assertion) {
        return new ReturnValueCallExpectation<>(assertion);
    }

    /**
     * Alias for {@link this#assertValue(AssertionCall)}. Intended to be used
     * for example with RestAssured tests where the asserted value is an
     * HTTP response.
     * @param assertion Assertion
     * @param <VALUE_TYPE> Type of the response to assert
     * @return
     * @since 0.5
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> assertResponse(AssertionCall<VALUE_TYPE> assertion) {
        return assertValue(assertion);
    }

    /**
     * Shorthand method for asserting a single value. The following are
     * essentially the same:
     * <pre>assertValue(value -> assertThat(value, is(1))</pre>
     * <pre>expectValue(1)</pre>
     * @param value
     * @param <VALUE_TYPE>
     * @return
     * @since 0.5
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> expectValue(VALUE_TYPE value) {
        return new ReturnValueCallExpectation<>(value);
    }

}
