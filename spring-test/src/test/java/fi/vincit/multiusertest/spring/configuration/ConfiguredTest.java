package fi.vincit.multiusertest.spring.configuration;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;

public abstract class ConfiguredTest extends AbstractUserRoleIT<User, User.Role> {

    private static Map<String, User> users = new HashMap<>();

    @Before
    public void init() {
        authorization().setExpectedException(AccessDeniedException.class);
    }

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
