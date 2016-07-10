package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Utility class for defining multiple users with less typing.
 * Use {@link UserIdentifiers#users(String...)} to initialize.
 * Contains a collection of user names
 */
public class Users implements UserIdentifierCollection {

    private Collection<String> userIdentifiers;

    /**
     * Initializes the Users with the given user names
     * @param usernames
     */
    public Users(String... usernames) {
        userIdentifiers = new ArrayList<>(usernames.length);
        for (int i = 0; i < usernames.length; ++i) {
            Objects.requireNonNull(usernames[i], "Username must not be null: username at index " + i + " was");
            userIdentifiers.add(new UserIdentifier(UserIdentifier.Type.USER, usernames[i]).toString());
        }
    }

    @Override
    public Collection<String> getUserIdentifiers() {
        return userIdentifiers;
    }
}
