package fi.vincit.multiusertest.rule;

public class Expectation {

    public static ExpectValueOf valueOf(ExpectValueOfCallback value) {
        return new ExpectValueOf(value);
    }

    public static ExpectCall call(FunctionCall functionCall) {
        return new ExpectCall(functionCall);
    }

}
