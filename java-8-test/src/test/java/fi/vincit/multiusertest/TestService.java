package fi.vincit.multiusertest;

public class TestService {
    public void throwIllegalStateException() {
        throw new IllegalStateException("Denied");
    }

    public void noThrow() {
    }

    public int returnsValue(int value) {
        return value;
    }
}
