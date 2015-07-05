package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Abstract test class implementation that can be used for running MultiUserTestRunner tests
 * with just a system specific configurations.
 *
 * @param <USER> User class
 * @param <ROLE> Role
 */
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractUserRoleIT<USER, ROLE> {

    private TestUser<USER, ROLE> user;
    private TestUser<USER, ROLE> creator;

    private Random random = new Random(System.currentTimeMillis());

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Before
    @Transactional
    public void initializeUsers() {
        initializeCreator();
        initializeUser();

        // By default log in as creator so that user doesn't have to do it every time
        logInAs(LoginRole.CREATOR);
    }

    private void initializeUser() {
        if (user.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            this.user = user.withUser(createUser(getRandomUsername(), "Test", "User", getUserRole(), LoginRole.USER));
        } else if (user.getMode() == TestUser.RoleMode.CREATOR_USER) {
            this.user = user.withUser(creator.getUser());
        } else if (user.getMode() == TestUser.RoleMode.NEW_WITH_CREATOR_ROLE) {
            this.user = user.withUser(createUser(getRandomUsername(), "Test", "User", getCreatorRole(), LoginRole.USER));
        } else if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + user.getMode());
        }
    }

    private void initializeCreator() {
        if (creator.getMode() == TestUser.RoleMode.SET_USER_ROLE) {
            this.creator = creator.withUser(createUser(getRandomUsername(), "Test", "Creator", creator.getRole(), LoginRole.CREATOR));
        } else if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creator.getMode());
        }
    }

    public AuthorizationRule authorization() {
        return authorizationRule;
    }

    public void setUsers(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        setCreatorIdentifier(creatorIdentifier);
        setUserIdentifier(userIdentifier);
    }

    public USER getUser() {
        if (user.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return getUserByUsername(user.getIdentifier());
        } else {
            return user.getUser();
        }
    }

    public USER getCreator() {
        if (creator.getMode() == TestUser.RoleMode.EXISTING_USER) {
            return getUserByUsername(creator.getIdentifier());
        } else {
            return creator.getUser();
        }
    }

    protected ROLE getUserRole() {
        return user.getRole();
    }

    protected void logInAs(LoginRole role) {
        if (role == LoginRole.CREATOR) {
            USER creatorUser = getCreator();
            assertThat("Trying to log in as creator but the creator doesn't exist", creatorUser, notNullValue());
            loginWithUser(creatorUser);

            authorizationRule.setRole(UserIdentifier.Type.CREATOR, null);
        } else {
            USER user = getUser();
            assertThat("Trying to log in as user but the user doesn't exist", user, notNullValue());
            loginWithUser(user);

            if (this.user.getMode() == TestUser.RoleMode.EXISTING_USER) {
                authorizationRule.setRole(UserIdentifier.Type.USER, this.user.getIdentifier());
            } else if (this.user.getMode() == TestUser.RoleMode.CREATOR_USER) {
                authorizationRule.setRole(UserIdentifier.getCreator());
            } else {
                authorizationRule.setRole(UserIdentifier.Type.ROLE, getUserRole().toString());
            }
        }
    }

    protected ROLE getCreatorRole() {
        return creator.getRole();
    }

    String getRandomUsername() {
        return "testuser" + random.nextInt();
    }

    private void setCreatorIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.USER) {
            this.creator = TestUser.forExistingUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            this.creator = TestUser.forRole(
                    stringToRole(identifier.getIdentifier()),
                    identifier
            );
        } else {
            throw new IllegalArgumentException("Invalid identifier for creator: " + identifier.getType());
        }
    }

    private void setUserIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.CREATOR) {
            this.user = TestUser.forCreatorUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.NEW_USER) {
            this.user = TestUser.forNewUser(getCreatorRole(), identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            this.user = TestUser.forRole(stringToRole(identifier.getIdentifier()), identifier);
        } else {
            this.user = TestUser.forExistingUser(identifier);
        }
    }

    protected TestUser<USER, ROLE> getUserModel() {
        return user;
    }

    protected TestUser<USER, ROLE> getCreatorModel() {
        return creator;
    }

    /**
     * Log in user with given user
     * @param user User
     */
    protected abstract void loginWithUser(USER user);

    /**
     * Creates new user to the system and returns it
     * @param username Random user name
     * @param firstName First name
     * @param lastName Last name
     * @param userRole User role
     * @param loginRole Login role
     * @return Created user
     */
    protected abstract USER createUser(String username, String firstName, String lastName, ROLE userRole, LoginRole loginRole);

    /**
     * Returns given role string as system role object/enum.
     * @param role Role as string.
     * @return Role object/enum
     */
    protected abstract ROLE stringToRole(String role);

    /**
     * Search user by username
     * @param username User's username
     * @return User object
     */
    protected abstract USER getUserByUsername(String username);
}
