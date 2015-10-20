package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;

public class UserContainer<USER, ROLE> {

    private TestUser<ROLE> creator;
    private USER creatorUser;
    private TestUser<ROLE> user;
    private USER userUser;
    private UserFactory<USER, ROLE> userFactory;

    public UserContainer(UserFactory<USER, ROLE> userFactory, TestUser<ROLE> creator, TestUser<ROLE> user) {
        this.userFactory = userFactory;
        this.creator = creator;
        this.user = user;
    }

    public static <USER, ROLE> UserContainer<USER, ROLE> initialize(UserFactory<USER, ROLE> userFactory, TestUser<ROLE> creator, TestUser<ROLE> user) {
        UserContainer<USER, ROLE> container = new UserContainer<>(userFactory, creator, user);

        container.initializeCreator(creator, userFactory);
        container.initializeUser(user, userFactory);
        return container;
    }

    private void initializeUser(TestUser<ROLE> user, UserFactory<USER, ROLE> userFactory) {
        if (user.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            userUser = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", user.getRole(), LoginRole.USER);
        } else if (user.getMode() == TestUser.RoleMode.CREATOR_USER) {
            if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // Do nothing, user already set, using creator
            } else {
                userUser = creatorUser;
            }
        } else if (user.getMode() == TestUser.RoleMode.NEW_WITH_CREATOR_ROLE) {
            if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
                // NOOP
            } else {
                userUser = userFactory.createUser(userFactory.getRandomUsername(), "Test", "User", creator.getRole(), LoginRole.USER);
            }
        } else if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else if (user.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + user.getMode());
        }
    }

    private void initializeCreator(TestUser<ROLE> creator, UserFactory<USER, ROLE> userFactory) {
        if (creator.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            creatorUser = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Creator", creator.getRole(), LoginRole.CREATOR);
        } else if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else if (creator.getMode() == TestUser.RoleMode.ANONYMOUS) {
            // Do nothing, user is not used
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creator.getMode());
        }
    }

    public USER getUser() {
        if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(user.getIdentifier());
        } else if (user.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return userUser;
        }
    }

    public USER getCreator() {
        if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(creator.getIdentifier());
        } else if (creator.getMode() == TestUser.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return creatorUser;
        }
    }

}
