package fi.vincit.multiusertest.rule;

public class Expectation {

    public static <T> ExpectValueOf<T> valueOf(ExpectValueOfCallback<T> value) {
        return new ExpectValueOf<T>(value);
    }

    public static ExpectCall call(FunctionCall functionCall) {
        return new ExpectCall(functionCall);
    }

}
