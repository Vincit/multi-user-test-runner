package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Generic definition of a user to use in the tests. Defines what kind of
 * user is being used in the tests. E.g. is user created with a certain role or is an existing
 * user used.
 */
public class UserIdentifier {

    public static final String ROLE_SPLITTER = ":";
    public static final String IDENTIFIER_SPLITTER = ":";
    // $ was chosen so that it can be used also in Java variables/constants
    // e.g. RunWithUsers.$PRODUCER
    public static final String FOCUS_PREFIX = "$";


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
    private final Collection<String> identifierParts;
    private final FocusType focusMode;

    /**
     * Parses a user identifier string and creates a {@link UserIdentifier} instance.
     * user identifier string is in format <i>&lt;type&gt;:&lt;identifier&gt;</i> e.g. <i>user:admin</i> or <i>role:ROLE_ADMIN</i>
     * @param identifierString User identifier string to parse
     * @return UserIdentifier object
     */
    public static UserIdentifier parse(String identifierString) {
        FocusType focusMode = FocusType.NONE;
        if (identifierString.startsWith(FOCUS_PREFIX)) {
            focusMode = FocusType.FOCUS;
            identifierString = identifierString.substring(1);
        }

        if (identifierString.equals(RunWithUsers.PRODUCER)) {
            return getProducer(focusMode);
        } else if (identifierString.equals(RunWithUsers.WITH_PRODUCER_ROLE)) {
            return getWithProducerRole(focusMode);
        } else if (identifierString.equals(RunWithUsers.ANONYMOUS)) {
            return getAnonymous(focusMode);
        } else if (identifierString.startsWith(MultiUserTestRunner.USER_PREFIX) || identifierString.startsWith(MultiUserTestRunner.ROLE_PREFIX)) {
            String[] data = identifierString.split(IDENTIFIER_SPLITTER, 2);
            return new UserIdentifier(Type.valueOf(data[0].toUpperCase()), data[1], focusMode);
        } else {
            throw new IllegalArgumentException("invalid producer parameter: <" + identifierString +
                    ">. Parameter has to start with \"$role:\", \"role\", \"user:\" or \"$user\" or it has to be RunWithUsers.PRODUCER or RunWithUsers.WITH_PRODUCER_ROLE.");
        }
    }

    /**
     * Utility method for splitting multi-role identifier to Strings
     * and mapping them to wanted type.
     * An utility method is used to preserve backwards compatibility.
     * In future major version updates this may change.
     * @since 0.5
     * @param identifier Identifier without type
     * @param mapper Mapper function for String -&gt; role type mapping
     * @param <T> Type to which the roles are mapped to
     * @return One or more identifiers
     */
    public static <T> Collection<T> mapMultiRoleIdentifier(String identifier, Function<String, T> mapper) {
        if (identifier != null) {
            return Stream.of(identifier.split(ROLE_SPLITTER))
                    .filter(role -> !role.isEmpty())
                    .map(mapper)
                    .collect(toSet());
        } else {
            return Collections.emptySet();
        }
    }

    public static UserIdentifier getAnonymous(FocusType focusType) {
        return new UserIdentifier(Type.ANONYMOUS, null, focusType);
    }

    public static UserIdentifier getProducer(FocusType focusType) {
        return new UserIdentifier(Type.PRODUCER, null, focusType);
    }

    public static UserIdentifier getWithProducerRole(FocusType focusType) {
        return new UserIdentifier(Type.WITH_PRODUCER_ROLE, null, focusType);
    }

    public static UserIdentifier getAnonymous() {
        return getAnonymous(FocusType.NONE);
    }

    public static UserIdentifier getProducer() {
        return getProducer(FocusType.NONE);
    }

    public static UserIdentifier getWithProducerRole() {
        return getWithProducerRole(FocusType.NONE);
    }

    /**
     * Creates a new {@link UserIdentifier} with the given type and identifier.
     * Usually objects are instantiated using {@link #parse(String)} for creating objects.
     * @param type Role type
     * @param identifier Identifier (user name or role name)
     */
    public UserIdentifier(Type type, String identifier) {
        this(type, identifier, FocusType.NONE);
    }

    /**
     * Creates a new {@link UserIdentifier} with the given type and identifier.
     * Usually objects are instantiated using {@link #parse(String)} for creating objects.
     * @param type Role type
     * @param identifier Identifier (user name or role name)
     * @param focusMode Focus mode to use
     */
    public UserIdentifier(Type type, String identifier, FocusType focusMode) {
        this.type = type;
        this.identifier = identifier;
        this.identifierParts = mapMultiRoleIdentifier(identifier, Function.identity());
        this.focusMode = focusMode;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public FocusType getFocusMode() {
        return focusMode;
    }

    @Override
    public String toString() {
        final String typeString = type.toString().toLowerCase();
        if (identifier != null) {
            return typeString + IDENTIFIER_SPLITTER + identifier;
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

        if (identifierParts != null) {
            return identifierParts.equals(that.identifierParts);
        } else {
            return that.identifierParts == null;
        }

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (identifierParts != null ? identifierParts.hashCode() : 0);
        return result;
    }
}
