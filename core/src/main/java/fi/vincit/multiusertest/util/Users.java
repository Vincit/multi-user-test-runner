package fi.vincit.multiusertest.util;

import java.util.Objects;

public class Users implements UserIdentifierCollection {

    private String[] userIdentifiers;

    public Users(String... usernames) {
        userIdentifiers = new String[usernames.length];
        for (int i = 0; i < usernames.length; ++i) {
            Objects.requireNonNull(usernames[i], "Username must not be null: username at index " + i + " was");
            userIdentifiers[i] = new UserIdentifier(UserIdentifier.Type.USER, usernames[i]).toString();
        }
    }

    @Override
    public String[] getUserIdentifiers() {
        return userIdentifiers;
    }
}
