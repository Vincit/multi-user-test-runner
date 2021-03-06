package fi.vincit.multiusertest.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GenericUserIdentifierCollection implements UserIdentifierCollection {

    private final Collection<UserIdentifier> identifiers;

    public GenericUserIdentifierCollection(Collection<UserIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public GenericUserIdentifierCollection(UserIdentifier... identifiers) {
        this.identifiers = Arrays.asList(identifiers);
    }

    @Override
    public Collection<UserIdentifier> getUserIdentifiers() {
        return identifiers;
    }
}
