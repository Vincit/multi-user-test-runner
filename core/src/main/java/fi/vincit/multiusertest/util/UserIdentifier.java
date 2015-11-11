package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;

/**
 * Generic definition of a user to use in the tests. Defines what kind of
 * user is being used in the tests. E.g. is user created with a certain role or is an existing
 * user used.
 */
public class UserIdentifier {

    /**
     * Definitions for user types. Specifies what kind of
     * user is created for logging in or should an existing user be used.
     */
    public enum Type {
        /**
         * Same as producer user
         */
        PRODUCER,
        /**
         * New user with the same role as the producer used
         */
        WITH_PRODUCER_ROLE,
        /**
         * User with certain role.
         */
        ROLE,
        /**
         * Existing user. No new user created.
         */
        USER,
        /**
         * Not logged in user.
         */
        ANONYMOUS
    }

    private final Type type;
    private final String identifier;

    public static UserIdentifier parse(String identifierString) {
        if (identifierString.equals(RunWithUsers.PRODUCER)) {
            return getProducer();
        } else if (identifierString.equals(RunWithUsers.WITH_PRODUCER_ROLE)) {
            return getWithProducerRole();
        } else if (identifierString.equals(RunWithUsers.ANONYMOUS)) {
            return getAnonymous();
        } else if (identifierString.startsWith(MultiUserTestRunner.USER_PREFIX) || identifierString.startsWith(MultiUserTestRunner.ROLE_PREFIX)) {
            String[] data = identifierString.split(":", 2);
            return new UserIdentifier(Type.valueOf(data[0].toUpperCase()), data[1]);
        } else {
            throw new IllegalArgumentException("invalid producer parameter: <" + identifierString +
                    ">. Parameter has to start with \"role:\" or \"user:\" or it has to be RunWithUsers.PRODUCER or RunWithUsers.WITH_PRODUCER_ROLE.");
        }
    }

    public static UserIdentifier getAnonymous() {
        return new UserIdentifier(Type.ANONYMOUS, null);
    }

    public static UserIdentifier getProducer() {
        return new UserIdentifier(Type.PRODUCER, null);
    }

    public static UserIdentifier getWithProducerRole() {
        return new UserIdentifier(Type.WITH_PRODUCER_ROLE, null);
    }

    public UserIdentifier(Type type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        final String typeString = type.toString().toLowerCase();
        if (identifier != null) {
            return typeString + ":" + identifier;
        } else {
            return typeString;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserIdentifier that = (UserIdentifier) o;

        if (type != that.type) {
            return false;
        }

        if (identifier != null) {
            return identifier.equals(that.identifier);
        } else {
            return that.identifier == null;
        }

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
        return result;
    }
}
