package fi.vincit.multiusertest.rule.expectation;

public class CallbackCalled {

    private boolean called;

    public Object markCalled() {
        this.called = true;
        return this;
    }

    public boolean wasCalled() {
        return this.called;
    }
}
