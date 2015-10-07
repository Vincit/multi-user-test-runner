package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;

public class IdentifierResolver<USER, ROLE> {

    private TestUser<USER, ROLE> user;
    private TestUser<USER, ROLE> creator;

    public IdentifierResolver(TestUser<USER, ROLE> user, TestUser<USER, ROLE> creator) {
        this.user = user;
        this.creator = creator;
    }

    public UserIdentifier getIdentifierFor(LoginRole loginRole) {
        if (loginRole == LoginRole.CREATOR) {
            return getCreatorIdentifier();
        } else {
            return getUserIdentifier();
        }
    }

    private UserIdentifier getUserIdentifier() {
        TestUser.RoleMode roleMode = user.getMode();

        if (roleMode == TestUser.RoleMode.EXISTING_USER) {
            return new UserIdentifier(UserIdentifier.Type.USER, user.getIdentifier());
        } else if (roleMode == TestUser.RoleMode.CREATOR_USER) {
            return UserIdentifier.getCreator();
        } else if (roleMode == TestUser.RoleMode.ANONYMOUS) {
            return UserIdentifier.getAnonymous();
        } else if (roleMode == TestUser.RoleMode.NEW_WITH_CREATOR_ROLE) {
            return new UserIdentifier(UserIdentifier.Type.ROLE, creator.getIdentifier());
        } else {
            return new UserIdentifier(UserIdentifier.Type.ROLE, user.getIdentifier());
        }
    }

    private UserIdentifier getCreatorIdentifier() {
        return UserIdentifier.getCreator();
    }
}
