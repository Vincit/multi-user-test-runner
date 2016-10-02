package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Basic example for configuring MUTR config class. This config class
 * uses Spring Framework dependency injection to autowire utility bean.
 *
 * Basically this class configures:
 * <ol>
 *     <li>How to convert test class role definitions (e.g. role:ROLE_ADMIN) to real system used role types whether it is enum, string or something else</li>
 *     <li>How create new user to the system</li>
 *     <li>How to login in to the system</li>
 *     <li>How to get system user by username</li>
 * </ol>
 */
public class TestMultiUserConfig extends AbstractMultiUserConfig<User, Role> {

    @Autowired
    private UserService userService;

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(user);
    }

    @Override
    public User createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
        return userService.createUser(username, username, userRole);
    }

    @Override
    public Role stringToRole(String role) {
        return Role.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
