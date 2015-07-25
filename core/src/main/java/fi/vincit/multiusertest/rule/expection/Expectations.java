package fi.vincit.multiusertest.rule.expection;

import fi.vincit.multiusertest.rule.expection.call.ExpectCall;
import fi.vincit.multiusertest.rule.expection.value.ExpectValueOf;

public class Expectations {

    public static <T> ExpectValueOf<T> valueOf(ReturnValueCall<T> value) {
        return new ExpectValueOf<T>(value);
    }

    public static ExpectCall call(FunctionCall functionCall) {
        return new ExpectCall(functionCall);
    }

}
