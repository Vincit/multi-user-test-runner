package fi.vincit.multiusertest.util;

public class Calls {
    private final int expected;
    private int called;

    public Calls(int expected) {
        this.expected = expected;
    }

    public int getExpected() {
        return expected;
    }

    public int getCalled() {
        return called;
    }

    public void call() {
        ++called;
    }

    public static Calls expected(int expected) {
        return new Calls(expected);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("expected=").append(expected);
        sb.append(", called=").append(called);
        return sb.toString();
    }
}
