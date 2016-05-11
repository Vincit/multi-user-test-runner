package fi.vincit.multiusertest.context;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestMultiUserConfig extends AbstractMultiUserConfig<User, User.Role> {

    public TestMultiUserConfig() {
    }

    @Autowired
    private UserService userService;

    @Override
    public void loginWithUser(User user) {
        if (user != null) {
            SecurityUtil.logInUser(user);
        } else {
            SecurityUtil.logInUser(null);
        }
    }

    @Override
    public void loginAnonymous() {
        throw new IllegalStateException("Anonymous not supported in this test");
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
