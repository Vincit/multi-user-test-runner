package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class Roles implements UserIdentifierCollection {

    private Collection<String> roleIdentifiers;

    public Roles(String... roles) {
        roleIdentifiers = new ArrayList<>(roles.length);
        for (int i = 0; i < roles.length; ++i) {
            Objects.requireNonNull(roles[i], "Role must not be null: role at index " + i + " was");
            roleIdentifiers.add(new UserIdentifier(UserIdentifier.Type.ROLE, roles[i]).toString());
        }
    }

    @Override
    public Collection<String> getUserIdentifiers() {
        return roleIdentifiers;
    }
}
