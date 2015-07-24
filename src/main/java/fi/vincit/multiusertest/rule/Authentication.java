package fi.vincit.multiusertest.rule;

import java.util.ArrayList;
import java.util.List;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

public class Authentication {

    private List<UserIdentifier> identifiers;
    private FailMode failMode;

    public static Authentication notToFail(UserIdentifiers condition) {
        return new Authentication(FailMode.EXPECT_NOT_FAIL).ifAnyOfIdentifiers(condition.getIdentifiers());
    }

    public static Authentication toFail(UserIdentifiers condition) {
        return new Authentication(FailMode.EXPECT_FAIL).ifAnyOfIdentifiers(condition.getIdentifiers());
    }

    public static UserIdentifiers ifAnyOf(String... identifiers) {
        return new UserIdentifiers(identifiers);
    }

    private Authentication ifAnyOfIdentifiers(String... identifiers) {
        this.identifiers = new ArrayList<>();

        for (String identifier : identifiers) {
            this.identifiers.add(UserIdentifier.parse(identifier));
        }

        return this;
    }

    private Authentication ifAnyOfIdentifiers(List<UserIdentifier> userIdentifiers) {
        this.identifiers = userIdentifiers;
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
