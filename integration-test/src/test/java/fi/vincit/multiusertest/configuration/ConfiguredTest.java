package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfiguredTest extends AbstractUserRoleIT<User, User.Role> {

    private static Map<String, User> users = new HashMap<>();

    @Override
    protected void loginWithUser(User user) {
        SecurityUtil.logInUser(user);
    }

    @Override
    protected User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        User user = new User(username, userRole);
        users.put(username, user);
        return user;
    }

    @Override
    protected User.Role stringToRole(String role) {
        return User.Role.valueOf(role);
    }

    @Override
    protected User getUserByUsername(String username) {
        return users.get(username);
    }
}
