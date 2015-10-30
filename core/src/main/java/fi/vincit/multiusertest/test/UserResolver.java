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
    private final RoleConverter<ROLE> roleConverter;

    public UserResolver(UserFactory<USER, ROLE> userFactory, RoleConverter<ROLE> roleConverter, UserIdentifier creator, UserIdentifier user) {
        this.userFactory = userFactory;
        this.roleConverter = roleConverter;
        this.creatorRoleContainer = resolveCreatorFromIdentifier(creator);
        this.userRoleContainer = resolveUserFromIdentifier(user);
    }

    private RoleContainer<ROLE> resolveCreatorFromIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.USER) {
            return RoleContainer.forExistingUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return RoleContainer.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return RoleContainer.forRole(
                    roleConverter.stringToRole(identifier.getIdentifier()),
                    identifier
            );
        } else {
            throw new IllegalArgumentException("Invalid identifier for creator: " + identifier.getType());
        }
    }

    private RoleContainer<ROLE> resolveUserFromIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.CREATOR) {
            return RoleContainer.forCreatorUser();
        } else if (identifier.getType() == UserIdentifier.Type.NEW_USER) {
            if (creatorRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
                throw new IllegalStateException("Cannot use NEW_USER mode when creator uses existing user");
            }
            return RoleContainer.forNewUser(creatorRoleContainer.getRole(), identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return RoleContainer.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return RoleContainer.forRole(roleConverter.stringToRole(identifier.getIdentifier()), identifier);
        } else {
            return RoleContainer.forExistingUser(identifier);
        }
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
