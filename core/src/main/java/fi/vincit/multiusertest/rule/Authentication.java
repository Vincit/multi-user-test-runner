package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.List;

/**
 * Rule for defining which when method under test should fail and not fail.
 * Used with {@link AuthorizationRule#expect(Authentication)}
 */
public class Authentication {

    private final UserIdentifiers identifiers;
    private final FailMode failMode;

    /**
     * Rule for method under test not to fail
     * @param condition User identifiers for which test should not fail
     * @return
     */
    public static Authentication notToFail(UserIdentifiers condition) {
        return new Authentication(FailMode.EXPECT_NOT_FAIL, condition);
    }

    /**
     * Rule for method under test to fail
     * @param condition User identifiers for which test should fail
     * @return
     */
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
