package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.rule.ExpectAuthenticationDeniedForUser;
import fi.vincit.multiusertest.runner.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
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
 * @param <USER_ID> User objects ID type
 * @param <ROLE> Role
 */
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractUserRoleIT<USER, USER_ID, ROLE> {

    private enum RoleMode {
        CREATOR_USER,
        NEW_WITH_CREATOR_ROLE,
        SET_USER_ROLE,
        EXISTING_USER
    }

    private ROLE userRole;
    private RoleMode userMode;
    private USER user;
    private UserIdentifier userIdentifier;

    private ROLE creatorRole;
    private RoleMode creatorMode;
    private USER creator;
    private UserIdentifier creatorIdentifier;

    private Random random = new Random(System.currentTimeMillis());

    @Rule
    public ExpectAuthenticationDeniedForUser expectAuthenticationDeniedForUser =
            new ExpectAuthenticationDeniedForUser();

    @Before
    @Transactional
    public void initializeUsers() {
        initializeCreator();
        initializeUser();

        // By default log in as creator so that user doesn't have to do it every time
        logInAs(LoginRole.CREATOR);
    }

    private void initializeUser() {
        if (userMode == RoleMode.SET_USER_ROLE) {
            user = createUser(getRandomUsername(), "Test", "User", getUserRole(), LoginRole.USER);
        } else if (userMode == RoleMode.CREATOR_USER) {
            setUser(creator);
        } else if (userMode == RoleMode.NEW_WITH_CREATOR_ROLE) {
            user = createUser(getRandomUsername(), "Test", "User", getCreatorRole(), LoginRole.USER);
        } else if (userMode == RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else {
            throw new IllegalArgumentException("Invalid user mode: " + creatorMode);
        }
    }

    private void initializeCreator() {
        if (creatorMode == RoleMode.SET_USER_ROLE) {
            creator = createUser(getRandomUsername(), "Test", "Creator", creatorRole, LoginRole.CREATOR);
        } else if (creatorMode == RoleMode.EXISTING_USER) {
            // Do nothing, user already set
        } else {
            throw new IllegalArgumentException("Invalid creator user mode: " + creatorMode);
        }
    }

    public ExpectAuthenticationDeniedForUser authorization() {
        return expectAuthenticationDeniedForUser;
    }

    public void setUsers(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        setCreatorIdentifier(creatorIdentifier);
        setUserIdentifier(userIdentifier);
    }

    protected void setUser(USER user) {
        this.user = user;
    }

    protected USER getUser() {
        if (userMode == RoleMode.EXISTING_USER) {
            return getUserByUsername(userIdentifier.getIdentifier());
        } else {
            return user;
        }
    }

    public USER getCreator() {
        if (creatorMode == RoleMode.EXISTING_USER) {
            return getUserByUsername(creatorIdentifier.getIdentifier());
        } else {
            return creator;
        }
    }

    protected ROLE getUserRole() {
        return userRole;
    }

    protected void logInAs(LoginRole role) {
        if (role == LoginRole.CREATOR) {
            USER creatorUser = getCreator();
            assertThat("Trying to log in as creator but the creator doesn't exist", creatorUser, notNullValue());
            loginWithUser(creatorUser);

            expectAuthenticationDeniedForUser.setRole(UserIdentifier.Type.CREATOR, null);
        } else {
            USER user = getUser();
            assertThat("Trying to log in as user but the user doesn't exist", user, notNullValue());
            loginWithUser(user);

            if (this.userMode == RoleMode.EXISTING_USER) {
                expectAuthenticationDeniedForUser.setRole(UserIdentifier.Type.USER, this.userIdentifier.getIdentifier());
            } else if (this.userMode == RoleMode.CREATOR_USER) {
                expectAuthenticationDeniedForUser.setRole(UserIdentifier.getCreator());
            } else {
                expectAuthenticationDeniedForUser.setRole(UserIdentifier.Type.ROLE, getUserRole().toString());
            }
        }
    }

    protected ROLE getCreatorRole() {
        return creatorRole;
    }

    String getRandomUsername() {
        return "testuser" + random.nextInt();
    }

    private void setCreatorIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.USER) {
            this.creator = null;
            this.creatorRole = null;
            this.creatorMode = RoleMode.EXISTING_USER;
            this.creatorIdentifier = identifier;
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            this.creatorRole = stringToRole(identifier.getIdentifier());
            this.creatorMode = RoleMode.SET_USER_ROLE;
        } else {
            throw new IllegalArgumentException("Invalid identifier for creator: " + identifier.getType());
        }
    }

    private void setUserIdentifier(UserIdentifier identifier) {
        if (identifier.getType() == UserIdentifier.Type.CREATOR) {
            this.user = null;
            this.userRole = null;
            this.userMode = RoleMode.CREATOR_USER;
        } else if (identifier.getType() == UserIdentifier.Type.NEW_USER) {
            this.user = null;
            this.userRole = creatorRole;
            this.userMode = RoleMode.NEW_WITH_CREATOR_ROLE;
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            this.user = null;
            this.userRole = stringToRole(identifier.getIdentifier());
            this.userMode = RoleMode.SET_USER_ROLE;
        } else {
            this.userIdentifier = identifier;
            this.user = null;
            this.userRole = null;
            this.userMode = RoleMode.EXISTING_USER;
        }
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
