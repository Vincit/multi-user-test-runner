package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.util.Optional;

class CallInfo {
    private final FailMode failMode;
    private final Optional<Class<? extends Throwable>> exceptionClass;

    public CallInfo(FailMode failMode, Optional<Class<? extends Throwable>> exceptionClass) {
        validate(failMode, exceptionClass);
        this.failMode = failMode;
        this.exceptionClass = exceptionClass;
    }

    private void validate(FailMode failMode, Optional<Class<? extends Throwable>> exceptionClass) {
        if (failMode == FailMode.EXPECT_FAIL && !exceptionClass.isPresent()) {
            throw new IllegalArgumentException("Exception must be given when failMode " + FailMode.EXPECT_FAIL);
        }
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

    public boolean isExceptionExpected(Throwable e) {
        return exceptionClass.isPresent() && exceptionClass.get().isInstance(e);
    }

}
