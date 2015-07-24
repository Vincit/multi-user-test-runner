package fi.vincit.multiusertest.rule.expection;

import fi.vincit.multiusertest.rule.ExpectCall;
import fi.vincit.multiusertest.rule.ExpectValueOfCallback;
import fi.vincit.multiusertest.rule.FunctionCall;
import fi.vincit.multiusertest.rule.expection.value.ExpectValueOf;

public class Expectation {

    public static <T> ExpectValueOf<T> valueOf(ExpectValueOfCallback<T> value) {
        return new ExpectValueOf<T>(value);
    }

    public static ExpectCall call(FunctionCall functionCall) {
        return new ExpectCall(functionCall);
    }

}
