package fi.vincit.multiusertest.spring.configuration;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.context.UserService;
import fi.vincit.multiusertest.runner.junit.framework.SpringMultiUserTestClassRunner;
import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;

@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class,
        defaultException = AccessDeniedException.class
)
public abstract class ConfiguredTest extends AbstractUserRoleIT<User, User.Role> {

    @Autowired
    private UserService userService;

    @Before
    public void tearDown() {
        SecurityUtil.clear();
        userService.clear();
    }

    @Override
    public void loginWithUser(User user) {
        if (user != null) {
            SecurityUtil.logInUser(user);
        } else {
            SecurityUtil.logInUser(null);
        }
    }

    @Override
    public User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        return userService.addUser(new User(username, userRole));
    }

    @Override
    public User.Role stringToRole(String role) {
        return User.Role.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.findByUsername(username);
    }
}
