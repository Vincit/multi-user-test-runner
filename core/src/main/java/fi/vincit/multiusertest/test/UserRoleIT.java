package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;


public interface UserRoleIT<USER, ROLE> {
    /**
     * Sets user identifiers. Validates that they are valid. If they are invalid
     * throws an exception.
     * @param creatorIdentifier Creator identifier
     * @param userIdentifier User identifier
     * @throws IllegalArgumentException If one or more identifiers are invalid.
     */
    void setUsers(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier);

    /**
     * Returns the current user
     * @return Current user or null if anonymous
     */
    USER getUser();

    /**
     * Returns the current creator user
     * @return Current creator user or null if anonymous
     */
    USER getCreator();

    /**
     * Login as the given user login role using the configured creator or user.
     * @param role Which user should be logged in
     */
    void logInAs(LoginRole role);

    /**
     * Search user by username
     * @param username User's username
     * @return User object
     */
    USER getUserByUsername(String username);

    /**
     * Returns given role string as system role object/enum.
     * @param role Role as string.
     * @return Role object/enum
     */
    ROLE stringToRole(String role);

    /**
     * Creates new user to the system and returns it
     * @param username Random user name
     * @param firstName First name
     * @param lastName Last name
     * @param userRole User role
     * @param loginRole Login role
     * @return Created user
     */
    USER createUser(String username, String firstName, String lastName, ROLE userRole, LoginRole loginRole);

    /**
     * Log in user with given user
     * @param user User
     */
    void loginWithUser(USER user);

    /**
     * Returns the default expected exception configured to the class
     * @return Default expected exception
     */
    Class<? extends Throwable> getDefaultException();
}
