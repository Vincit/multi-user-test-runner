package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;

public interface UserFactory<USER, ROLE> {

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

    String getRandomUsername();

    /**
     * Search user by username
     * @param username User's username
     * @return User object
     */
    USER getUserByUsername(String username);

}
