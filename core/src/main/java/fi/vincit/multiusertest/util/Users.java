package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Users implements UserIdentifierCollection {

    private Collection<String> userIdentifiers;

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
