package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.util.Optional;

class CallInfo {
    private final FailMode failMode;
    private final Optional<Class<? extends Throwable>> exceptionClass;

    public CallInfo(FailMode failMode, Optional<Class<? extends Throwable>> exceptionClass) {
        this.failMode = failMode;
        this.exceptionClass = exceptionClass;
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
