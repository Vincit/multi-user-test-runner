package fi.vincit.multiusertest.rule;

import java.util.ArrayList;
import java.util.List;

import fi.vincit.multiusertest.util.UserIdentifier;

public class Authentication {

    private List<UserIdentifier> identifiers;
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
        this.identifiers = new ArrayList<>();

        for (String identifier : identifiers) {
            this.identifiers.add(UserIdentifier.parse(identifier));
        }

        return this;
    }

    private Authentication(FailMode failMode) {
        this.failMode = failMode;
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers;
    }

    public FailMode getFailMode() {
        return failMode;
    }


}
