package fi.vincit.multiusertest.rule;

import java.util.List;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

public class Authentication {

    private final UserIdentifiers identifiers;
    private final FailMode failMode;

    public static Authentication notToFail(UserIdentifiers condition) {
        return new Authentication(FailMode.EXPECT_NOT_FAIL, condition);
    }

    public static Authentication toFail(UserIdentifiers condition) {
        return new Authentication(FailMode.EXPECT_FAIL, condition);
    }

    private Authentication(FailMode failMode, UserIdentifiers userIdentifiers) {
        this.identifiers = userIdentifiers;
        this.failMode = failMode;
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers.getIdentifiers();
    }

    public FailMode getFailMode() {
        return failMode;
    }


}
