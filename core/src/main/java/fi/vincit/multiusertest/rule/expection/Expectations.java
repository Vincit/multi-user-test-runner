package fi.vincit.multiusertest.rule.expection;

import java.util.Objects;

import fi.vincit.multiusertest.rule.expection.call.ExpectCall;
import fi.vincit.multiusertest.rule.expection.value.ExpectValueOf;

/**
 * A set of helper methods for making assertion rules.
 */
public class Expectations {

    /**
     * Expect a return value of a method to be specified.
     * @param valueCall Function to call as an anonymous class, lambda or method reference.
     * @param <T> Return value type
     * @return ExpectValueOf object for defining assertion rules.
     */
    public static <T> ExpectValueOf<T> valueOf(ReturnValueCall<T> valueCall) {
        Objects.requireNonNull(valueCall, "Value call must not be null");
        return new ExpectValueOf<>(valueCall);
    }

    /**
     * Expect a function call to throw or not throw an exception.
     * @param functionCall Function to call as an anonymous class, lambda or method reference.
     * @return ExpectCall object for defining assertion rules.
     */
    public static ExpectCall call(FunctionCall functionCall) {
        Objects.requireNonNull(functionCall, "Function call must not be null");
        return new ExpectCall(functionCall);
    }

}
