package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for defining multiple users with less typing.
 * Use {@link UserIdentifiers#users(String...)} to initialize.
 * Contains a collection of user names
 */
public class Users extends GenericUserIdentifierCollection {

    private Users(Collection<UserIdentifier> identifiers) {
        super(identifiers);
    }

    /**
     * Initializes the Users with the given usernames
     * @param usernames Usernames of the users who are created
     * @return Created users
     */
    public static Users create(String... usernames) {
        List<UserIdentifier>  userIdentifiers = new ArrayList<>(usernames.length);
        for (int i = 0; i < usernames.length; ++i) {
            Objects.requireNonNull(usernames[i], "Username must not be null: username at index " + i + " was null");
            userIdentifiers.add(new UserIdentifier(UserIdentifier.Type.USER, usernames[i]));
        }

        return new Users(userIdentifiers);
    }

}
