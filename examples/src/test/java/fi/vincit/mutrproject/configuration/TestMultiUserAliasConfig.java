package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Example configuration that uses role aliases. Coverts role definitions
 * to roles used by the system under test. See {@link #stringToRole(String)}
 * method.
 */
public class TestMultiUserAliasConfig extends AbstractMultiUserConfig<User, Role> {

    @Autowired
    private UserService userService;

    public TestMultiUserAliasConfig() {
    }

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(user);
    }

    @After
    public void tearDown() {
        userService.logout();
    }

    @Override
    public User createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
        return userService.createUser(username, username, userRole);
    }

    @Override
    public Role stringToRole(String role) {
        if (role.equals("REGULAR")) {
            return Role.ROLE_USER;
        } else {
            return Role.valueOf("ROLE_" + role);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
