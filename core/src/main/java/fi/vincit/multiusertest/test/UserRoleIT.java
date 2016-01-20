package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;


public interface UserRoleIT<USER> {
    /**
     * Sets user identifiers. Validates that they are valid. If they are invalid
     * throws an exception.
     * @param producerIdentifier Producer identifier
     * @param consumerIdentifier Consumer identifier
     * @throws IllegalArgumentException If one or more identifiers are invalid.
     */
    void setUsers(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier);

    /**
     * Returns the current consumer user
     * @return Current consumer user or null if anonymous
     */
    USER getConsumer();

    /**
     * Returns the current producer user
     * @return Current producer user or null if anonymous
     */
    USER getProducer();

    /**
     * Login as the given user login role using the configured producer or consmer.
     * @param role Which user should be logged in
     */
    void logInAs(LoginRole role);

    /**
     * Log in user with given user
     * @param user User
     */
    void loginWithUser(USER user);

    /**
     * "Log in" anonymous user. By default users {@link #loginWithUser(Object)}
     * using null as the user. Can be overridden to change the behaviour.
     */
    void loginAnonymous();

    /**
     * Returns the default expected exception configured to the class
     * @return Default expected exception
     */
    Class<? extends Throwable> getDefaultException();

    Class<? extends Throwable> getDefaultException(Class<?> cls);
}
