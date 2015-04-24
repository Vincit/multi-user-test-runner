package fi.vincit.multiusertest.rule;

public class Authentication {

    private String[] identifiers;
    private FailMode failMode;

    public static Authentication notToFail() {
        return new Authentication(FailMode.EXPECT_NOT_FAIL);
    }

    public static Authentication toFail() {
        return new Authentication(FailMode.EXPECT_FAIL);
    }

    public Authentication ifAnyOf(String... identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    private Authentication(FailMode failMode) {
        this.failMode = failMode;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    public FailMode getFailMode() {
        return failMode;
    }

}
