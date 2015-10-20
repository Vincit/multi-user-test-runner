package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;


public interface UserRoleIT<USER> {
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
