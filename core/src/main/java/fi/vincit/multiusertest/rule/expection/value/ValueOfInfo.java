package fi.vincit.multiusertest.rule.expection.value;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.call.ExceptionAssertionCall;
import fi.vincit.multiusertest.rule.expection.call.ExpectationInfo;
import fi.vincit.multiusertest.util.Optional;

class ValueOfInfo<VALUE_TYPE> extends ExpectationInfo {
    private final Optional<VALUE_TYPE> value;
    private final Optional<AssertionCall<VALUE_TYPE>> assertionCallback;

    public ValueOfInfo(FailMode failMode, Optional<VALUE_TYPE> value, Optional<AssertionCall<VALUE_TYPE>> assertionCallback, Optional<Class<? extends Throwable>> exceptionClass, ExceptionAssertionCall exceptionAssertionCall) {
        super(failMode, exceptionClass, exceptionAssertionCall);
        this.value = value;
        this.assertionCallback = assertionCallback;

    }

    public Optional<VALUE_TYPE> getValue() {
        return value;
    }

    public Optional<AssertionCall<VALUE_TYPE>> getAssertionCallback() {
        return assertionCallback;
    }

}
