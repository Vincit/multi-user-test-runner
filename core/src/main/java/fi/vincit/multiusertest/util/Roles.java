package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Utility class for defining multiple roles with less typing.
 * Use {@link UserIdentifiers#roles(String...)} to initialize.
 * Contains a collection of roles names.
 */
public class Roles extends GenericUserIdentifierCollection {

    private Roles(Collection<UserIdentifier> identifiers) {
        super(identifiers);
    }

    /**
     * Initializes the Roles with the given role names
     * @param roles Role names
     */
    public static Roles create(String... roles) {
        List <UserIdentifier> roleIdentifiers = new ArrayList<>(roles.length);
        for (int i = 0; i < roles.length; ++i) {
            Objects.requireNonNull(roles[i], "Role must not be null: role at index " + i + " was null");
            roleIdentifiers.add(new UserIdentifier(UserIdentifier.Type.ROLE, roles[i]));
        }

        return new Roles(roleIdentifiers);
    }

}
