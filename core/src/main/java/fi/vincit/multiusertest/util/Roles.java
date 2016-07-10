package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Utility class for defining multiple roles with less typing.
 * Use {@link UserIdentifiers#roles(String...)} to initialize.
 * Contains a collection of roles names.
 */
public class Roles implements UserIdentifierCollection {

    private Collection<String> roleIdentifiers;

    /**
     * Initializes the Roles with the given role names
     * @param roles
     */
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
