package fi.vincit.multiusertest.rule.expection.value;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.call.ExceptionAssertionCall;
import fi.vincit.multiusertest.util.Optional;

class ValueOfInfo<VALUE_TYPE> {
    private final FailMode failMode;
    private final Optional<VALUE_TYPE> value;
    private final Optional<AssertionCall<VALUE_TYPE>> assertionCallback;
    private final Optional<Class<? extends Throwable>> exceptionClass;
    private final ExceptionAssertionCall exceptionAssertionCall;

    public ValueOfInfo(FailMode failMode, Optional<VALUE_TYPE> value, Optional<AssertionCall<VALUE_TYPE>> assertionCallback, Optional<Class<? extends Throwable>> exceptionClass, ExceptionAssertionCall exceptionAssertionCall) {
        this.failMode = failMode;
        this.value = value;
        this.assertionCallback = assertionCallback;
        this.exceptionClass = exceptionClass;
        this.exceptionAssertionCall = exceptionAssertionCall;
    }

    public Optional<VALUE_TYPE> getValue() {
        return value;
    }

    public Optional<AssertionCall<VALUE_TYPE>> getAssertionCallback() {
        return assertionCallback;
    }

    public Optional<Class<? extends Throwable>> getExceptionClass() {
        return exceptionClass;
    }

    public void assertException(Throwable e) {
        exceptionAssertionCall.assertException(e);
    }

    public boolean isExceptionExpected(Throwable e, Class<? extends Throwable> defaultException) {
        if (this.failMode == FailMode.EXPECT_NOT_FAIL) {
            return false;
        } else {
            return getException(defaultException).isInstance(e);
        }
    }

    private Class<? extends Throwable> getException(Class<? extends Throwable> defaultException) {
        return exceptionClass.orElse(defaultException);
    }

    public FailMode getFailMode() {
        return failMode;
    }
}
