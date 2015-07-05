package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.runner.MultiUserTestRunner;

/**
 *
 */
public class UserIdentifier {

    public enum Type {
        CREATOR,
        NEW_USER,
        ROLE,
        USER
    }

    private Type type;
    private String identifier;

    public static UserIdentifier parse(String identifierString) {
        if (identifierString.equals(TestUsers.CREATOR)) {
            return getCreator();
        } else if (identifierString.equals(TestUsers.NEW_USER)) {
            return getNewUser();
        } else if (identifierString.startsWith(MultiUserTestRunner.USER_PREFIX) || identifierString.startsWith(MultiUserTestRunner.ROLE_PREFIX)) {
            String[] data = identifierString.split(":", 2);
            return new UserIdentifier(Type.valueOf(data[0].toUpperCase()), data[1]);
        } else {
            throw new IllegalArgumentException("invalid creator parameter: <" + identifierString +
                    ">. Parameter has to start with \"role:\" or \"user:\" or it has to be TestUsers.CREATOR or TestUsers.NEW_USER.");
        }
    }

    public static UserIdentifier getCreator() {
        return new UserIdentifier(Type.CREATOR, null);
    }

    public static UserIdentifier getNewUser() {
        return new UserIdentifier(Type.NEW_USER, null);
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
