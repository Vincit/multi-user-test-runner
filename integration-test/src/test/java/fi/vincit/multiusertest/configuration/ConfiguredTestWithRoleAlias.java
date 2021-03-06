package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;


public class ConfiguredTestWithRoleAlias extends AbstractMultiUserConfig<User, User.Role> {

    private static Map<String, User> users = new HashMap<>();

    @Override
    public void loginWithUser(User user) {
        SecurityUtil.logInUser(user);
    }

    @After
    public void tearDown() {
        SecurityUtil.clear();
    }

    @Override
    public User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        User user = new User(username, userRole);
        users.put(username, user);
        return user;
    }

    @Override
    public User.Role stringToRole(String role) {
        switch(role) {
            case "NORMAL": return User.Role.ROLE_USER;
            case "ANONYMOUS": return User.Role.ROLE_VISITOR;
            default: return User.Role.valueOf("ROLE_" + role);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return users.get(username);
    }
}
