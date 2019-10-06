package fi.vincit.multiusertest.util;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Collections.singletonList;

public class User {
    public enum Role {
        ROLE_ADMIN,
        ROLE_USER,
        ROLE_SUPER_ADMIN,
        ROLE_VISITOR
    }

    private String username;
    private Collection<Role> role;

    public User(String username, Role role) {
        this.username = username;
        this.role = new HashSet<>(singletonList(role));
    }

    public User(String username, Collection<Role> role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role.iterator().next();
    }

    public Collection<Role> getRoles() {
        return role;
    }

    @Override
    public String toString() {
        return username + ":" + role;
    }
}
