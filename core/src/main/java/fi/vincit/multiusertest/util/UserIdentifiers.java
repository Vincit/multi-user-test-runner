package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.List;

public class UserIdentifiers {

    private static final UserIdentifiers EMPTY = new UserIdentifiers();

    private final List<UserIdentifier> identifiers = new ArrayList<>();

    public static UserIdentifiers empty() {
        return EMPTY;
    }

    public static UserIdentifiers ifAnyOf(String... identifiers) {
        return new UserIdentifiers(identifiers);
    }

    public static UserIdentifiers ifAnyOf(UserIdentifierCollection... identifiers) {
        List<String> allIdentifiers = new ArrayList<>();
        for (UserIdentifierCollection identifierCollection : identifiers) {
            allIdentifiers.addAll(identifierCollection.getUserIdentifiers());
        }
        return new UserIdentifiers(allIdentifiers.toArray(new String[allIdentifiers.size()]));
    }

    public static UserIdentifierCollection users(String... usernames) {
        return new Users(usernames);
    }

    public static UserIdentifierCollection roles(String... roles) {
        return new Roles(roles);
    }

    public UserIdentifiers(String... identifiers) {
        for (String identifier : identifiers) {
            this.identifiers.add(UserIdentifier.parse(identifier));
        }
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers;
    }

}
