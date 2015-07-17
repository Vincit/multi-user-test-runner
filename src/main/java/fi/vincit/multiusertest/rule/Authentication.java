package fi.vincit.multiusertest.rule;

public class Authentication {

    private String[] identifiers;
    private FailMode failMode;

    public static Authentication notToFail(Identifiers condition) {
        return new Authentication(FailMode.EXPECT_NOT_FAIL).ifAnyOfIdentifiers(condition.getIdentifiers());
    }

    public static Authentication toFail(Identifiers condition) {
        return new Authentication(FailMode.EXPECT_FAIL).ifAnyOfIdentifiers(condition.getIdentifiers());
    }

    public static Identifiers ifAnyOf(String... identifiers) {
        return new Identifiers(identifiers);
    }

    public static class Identifiers {
        private String[] identifiers;

        public Identifiers(String... identifiers) {
            this.identifiers = identifiers;
        }

        public String[] getIdentifiers() {
            return identifiers;
        }

    }

    private Authentication ifAnyOfIdentifiers(String... identifiers) {
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
