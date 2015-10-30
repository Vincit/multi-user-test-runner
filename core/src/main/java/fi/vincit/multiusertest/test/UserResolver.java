package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;

public class UserResolver<USER, ROLE> {

    private final RoleContainer<ROLE> creatorRoleContainer;
    private USER creator;

    private final RoleContainer<ROLE> userRoleContainer;
    private USER user;

    private final UserFactory<USER, ROLE> userFactory;

    public UserResolver(UserFactory<USER, ROLE> userFactory, RoleConverter<ROLE> roleConverter, UserIdentifier creator, UserIdentifier user) {
        this.userFactory = userFactory;
        this.creatorRoleContainer = RoleContainer.forCreator(creator, roleConverter);
        this.userRoleContainer = RoleContainer.forUser(user, creatorRoleContainer, roleConverter);
    }

    private void initializeUser() {
        if (userRoleContainer.getMode() == RoleContainer.RoleMode.SET_USER_ROLE) {
            user = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", userRoleContainer.getRole(), LoginRole.USER);
        } else if (userRoleContainer.getMode() == RoleContainer.RoleMode.CREATOR_USER) {
            if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
                // Do nothing, resolved in getter
            } else {
                user = creator;
            }
        } else if (userRoleContainer.getMode() == RoleContainer.RoleMode.NEW_WITH_CREATOR_ROLE) {
            if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
                // NOOP
            } else {
                user = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", creatorRoleContainer.getRole(), LoginRole.USER);
            }
        } else if (userRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            // Do nothing, resolved in getter
        } else if (userRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + userRoleContainer.getMode());
        }
    }

    private void initializeCreator() {
        if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.SET_USER_ROLE) {
            creator = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Creator", creatorRoleContainer.getRole(), LoginRole.CREATOR);
        } else if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            // Do nothing, resolved in getter
        } else if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creatorRoleContainer.getMode());
        }
    }

    public RoleContainer<ROLE> getCreator() {
        return creatorRoleContainer;
    }

    public RoleContainer<ROLE> getUser() {
        return userRoleContainer;
    }

    public USER resolveUser() {
        if (userRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(userRoleContainer.getIdentifier());
        } else if (userRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            return null;
        } else if (userRoleContainer.getMode() == RoleContainer.RoleMode.CREATOR_USER) {
            return resolverCreator();
        } else {
            return user;
        }
    }

    public USER resolverCreator() {
        if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(creatorRoleContainer.getIdentifier());
        } else if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return creator;
        }
    }

    public void resolve() {
        initializeCreator();
        initializeUser();
    }
}
