package fi.vincit.multiusertest.util;

public class Defaults {

    private Defaults() {
        // Util class
    }

    public static Class<? extends Throwable> getDefaultException() {
        return IllegalStateException.class;
    }

}
