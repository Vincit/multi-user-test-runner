package fi.vincit.multiusertest.util;

public class User {
    public enum Role {
        ROLE_ADMIN,
        ROLE_USER,
        ROLE_SUPER_ADMIN,
        ROLE_VISITOR
    }

    private String username;
    private Role role;

    public User(String username, Role role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return username + ":" + role;
    }
}
