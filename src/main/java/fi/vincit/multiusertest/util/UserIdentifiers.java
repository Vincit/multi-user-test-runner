package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.List;

public class UserIdentifiers {
    private List<UserIdentifier> identifiers;

    public UserIdentifiers(String... identifiers) {
        this.identifiers = new ArrayList<>();

        for (String identifier : identifiers) {
            this.identifiers.add(UserIdentifier.parse(identifier));
        }
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers;
    }

}
