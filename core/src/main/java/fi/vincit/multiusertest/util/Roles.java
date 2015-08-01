package fi.vincit.multiusertest.util;

import java.util.Objects;

public class Roles implements UserIdentifierCollection {

    private String[] roleIdentifiers;

    public Roles(String... roles) {
        roleIdentifiers = new String[roles.length];
        for (int i = 0; i < roles.length; ++i) {
            Objects.requireNonNull(roles[i], "Role must not be null: role at index " + i + " was");
            roleIdentifiers[i] = new UserIdentifier(UserIdentifier.Type.ROLE, roles[i]).toString();
        }
    }

    @Override
    public String[] getUserIdentifiers() {
        return roleIdentifiers;
    }
}
