package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.rule.expectation.call.FunctionCallExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation.call.FunctionCallNoExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation.value.ReturnValueCallExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation.value.ReturnValueCallExpectation;
import fi.vincit.multiusertest.rule.expectation.value.ReturnValueCallNoExceptionExpectation;
import fi.vincit.multiusertest.rule.expectation.value.TestValueExpectation;

/**
 * Expectation take a BDD like approach constructing
 * assertions and uses less nested calls.
 * @since 1.0
 */
public class TestExpectations {

    /**
     * Expect that an exception of the given type should be thrown
     * @param exception Exception expected
     * @return Expectation object
     * @since 1.0
     */
    public static <T extends Throwable> TestExpectation expectException(Class<T> exception) {
        return new FunctionCallExceptionExpectation<>(exception);
    }

    /**
     * Expect that an exception of the given type should be thrown and be
     * asserted with the given {@link AssertionCall}
     * @param exception Exception expected
     * @param assertion Custom assertion
     * @param <T> Type of the exception
     * @return Expectation object
     * @since 1.0
     */
    public static <T extends Throwable> TestExpectation expectException(Class<T> exception, AssertionCall<T> assertion) {
        return new FunctionCallExceptionExpectation<>(exception, assertion);
    }

    /**
     * Explicitly mark that a call shouldn't fail.
     * @return Expectation object
     * @since 1.0
     */
    public static TestExpectation expectNotToFail() {
        return new FunctionCallNoExceptionExpectation();
    }

    /**
     * Return value variant of {@link #expectNotToFail()}
     * @return Expectation object
     * @since 1.0
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> expectNotToFailIgnoringValue() {
        return new ReturnValueCallNoExceptionExpectation<>();
    }

    /**
     * Exception is expected instead of call under test returning a value
     * @param exception Exception expected
     * @param <VALUE_TYPE> Type of the return value of the call under test
     * @param <T> Type of the exception
     * @return Expectation object
     * @since 1.0
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
     * @return Expectation object
     * @since 1.0
     */
    public static <VALUE_TYPE, T extends Throwable> TestValueExpectation<VALUE_TYPE> expectExceptionInsteadOfValue(Class<T> exception, AssertionCall<T> assertionCall) {
        return new ReturnValueCallExceptionExpectation<>(exception, assertionCall);
    }

    /**
     * Assert returned value with the given {@link AssertionCall}
     * @param assertion Custom assertion
     * @param <VALUE_TYPE> Type of the value to be asserted
     * @return Expectation object
     * @since 1.0
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> assertValue(AssertionCall<VALUE_TYPE> assertion) {
        return new ReturnValueCallExpectation<>(assertion);
    }

    /**
     * Alias for {@link #assertValue(AssertionCall)}. Intended to be used
     * for example with RestAssured tests where the asserted value is an
     * HTTP response.
     * @param assertion Assertion
     * @param <VALUE_TYPE> Type of the response to assert
     * @return Expectation object
     * @since 1.0
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> assertResponse(AssertionCall<VALUE_TYPE> assertion) {
        return assertValue(assertion);
    }

    /**
     * Shorthand method for asserting a single value. The following are
     * essentially the same:
     * <pre>assertValue(value -> assertThat(value, is(1))</pre>
     * <pre>expectValue(1)</pre>
     * @param value Value expected
     * @param <VALUE_TYPE> Type of the value
     * @return Expectation object
     * @since 1.0
     */
    public static <VALUE_TYPE> TestValueExpectation<VALUE_TYPE> expectValue(VALUE_TYPE value) {
        return new ReturnValueCallExpectation<>(value);
    }


}
