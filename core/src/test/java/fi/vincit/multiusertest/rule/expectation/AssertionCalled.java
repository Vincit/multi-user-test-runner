package fi.vincit.multiusertest.rule.expectation;

public class AssertionCalled {

    private Throwable exception;

    public void withThrowable(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }
}
