package fi.vincit.multiusertest;

public class TestService {
    public void throwAccessDenied() {
        throw new IllegalStateException("Denied");
    }

    public void throwException(Throwable throwable) throws Throwable {
        throw throwable;
    }

    public void noThrow() {
    }

    public int returnsValue(int value) {
        return value;
    }
}
