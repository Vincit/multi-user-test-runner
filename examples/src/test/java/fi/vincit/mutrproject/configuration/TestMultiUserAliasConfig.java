package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserAndRoleConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * Example configuration that uses role aliases. Coverts role definitions
 * to roles used by the system under test. See {@link #stringToRole(String)}
 * method.
 */
public class TestMultiUserAliasConfig extends AbstractMultiUserAndRoleConfig<User, Role> {

    @Autowired
    private UserService userService;

    public TestMultiUserAliasConfig() {
    }

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(ofNullable(user).map(User::getUsername).orElse(null));
    }

    @After
    public void tearDown() {
        userService.logout();
    }

    @Override
    public User createUser(String username, String firstName, String lastName, Collection<Role> userRole, LoginRole loginRole) {
        return userService.createUser(username, username, userRole);
    }

    @Override
    public Collection<Role> stringToRole(String role) {
        final Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        if (role.equals("REGULAR")) {
            // No additional roles
        } else if (role.equals("ADMIN")) {
            roles.add(Role.ROLE_ADMIN);
            roles.add(Role.ROLE_MODERATOR);
        } else if (role.equals("SYSTEM_ADMIN")) {
            roles.add(Role.ROLE_ADMIN);
            roles.add(Role.ROLE_SYSTEM_ADMIN);
            roles.add(Role.ROLE_MODERATOR);
        } else {
            roles.add(Role.valueOf("ROLE_" + role));
        }
        return roles;
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
