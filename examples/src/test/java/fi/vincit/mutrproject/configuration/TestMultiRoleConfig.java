package fi.vincit.mutrproject.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserAndRoleConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * Example on how to configure users with multiple roles. Uses a custom role
 * {@link RoleGroup} to configure which roles to configure for the user. RoleGroup
 * can be any enum or object that just defines all the combinations that need to
 * be tested.
 */
public class TestMultiRoleConfig extends AbstractMultiUserAndRoleConfig<User, Role> {

    @Autowired
    private UserService userService;

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(ofNullable(user).map(User::getUsername).orElse(null));
    }

    @Override
    public User createUser(String username, String firstName, String lastName, Collection<Role> userRoles, LoginRole loginRole) {
        return userService.createUser(username, username, userRoles);
    }

    @Override
    protected Role identifierPartToRole(String identifier) {
        return Role.valueOf("ROLE_" + identifier);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
