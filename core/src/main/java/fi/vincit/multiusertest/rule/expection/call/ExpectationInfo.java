package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.util.Optional;

public class ExpectationInfo {

    private final FailMode failMode;
    private final Optional<Class<? extends Throwable>> exceptionClass;
    private final ExceptionAssertionCall exceptionAssertionCall;

    public ExpectationInfo(FailMode failMode, Optional<Class<? extends Throwable>> exceptionClass, ExceptionAssertionCall exceptionAssertionCall) {
        this.exceptionAssertionCall = exceptionAssertionCall;
        validate(failMode, exceptionClass);
        this.failMode = failMode;
        this.exceptionClass = exceptionClass;
    }

    private void validate(FailMode failMode, Optional<Class<? extends Throwable>> exceptionClass) {
        if (failMode == FailMode.EXPECT_NOT_FAIL && exceptionClass.isPresent()) {
            throw new IllegalArgumentException("Exception should not be given when failMode is " + FailMode.EXPECT_NOT_FAIL);
        }
        if (failMode == FailMode.NONE) {
            throw new IllegalArgumentException("Fail mode " + FailMode.NONE + " no supported");
        }
    }

    public FailMode getFailMode() {
        return failMode;
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

}
