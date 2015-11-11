package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;

public class IdentifierResolver<USER, ROLE> {

    private RoleContainer<ROLE> user;
    private RoleContainer<ROLE> creator;

    public IdentifierResolver(UserResolver<USER, ROLE> userResolver) {
        this.user = userResolver.getUser();
        this.creator = userResolver.getCreator();
    }

    public UserIdentifier getIdentifierFor(LoginRole loginRole) {
        if (loginRole == LoginRole.PRODUCER) {
            return getCreatorIdentifier();
        } else {
            return getUserIdentifier();
        }
    }

    private UserIdentifier getUserIdentifier() {
        RoleContainer.RoleMode roleMode = user.getMode();

        if (roleMode == RoleContainer.RoleMode.EXISTING_USER) {
            return new UserIdentifier(UserIdentifier.Type.USER, user.getIdentifier());
        } else if (roleMode == RoleContainer.RoleMode.CREATOR_USER) {
            return UserIdentifier.getCreator();
        } else if (roleMode == RoleContainer.RoleMode.ANONYMOUS) {
            return UserIdentifier.getAnonymous();
        } else if (roleMode == RoleContainer.RoleMode.NEW_WITH_CREATOR_ROLE) {
            if (creator.getMode() != RoleContainer.RoleMode.SET_USER_ROLE) {
                throw new IllegalStateException("Cannot use NEW_WITH_CREATOR_ROLE when creator doesn't have role");
            }
            return new UserIdentifier(UserIdentifier.Type.ROLE, creator.getIdentifier());
        } else {
            return new UserIdentifier(UserIdentifier.Type.ROLE, user.getIdentifier());
        }
    }

    private UserIdentifier getCreatorIdentifier() {
        return UserIdentifier.getCreator();
    }
}
