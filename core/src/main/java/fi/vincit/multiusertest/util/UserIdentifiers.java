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

    public UserIdentifiers(String... identifiers) {
        for (String identifier : identifiers) {
            this.identifiers.add(UserIdentifier.parse(identifier));
        }
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers;
    }

}
