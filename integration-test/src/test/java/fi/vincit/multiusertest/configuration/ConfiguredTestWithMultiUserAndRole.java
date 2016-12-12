package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.test.AbstractMultiUserAndRoleConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import org.junit.After;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ConfiguredTestWithMultiUserAndRole extends AbstractMultiUserAndRoleConfig<User, User.Role> {

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
    public User createUser(String username, String firstName, String lastName, Collection<User.Role> userRole, LoginRole loginRole) {
        User user = new User(username, userRole.iterator().next());
        users.put(username, user);
        return user;
    }

    @Override
    public User.Role identifierPartToRole(String identifierToRole) {
        switch(identifierToRole) {
            case "ADMIN": return User.Role.ROLE_ADMIN;
            case "USER": return User.Role.ROLE_USER;
            case "SUPER_ADMIN": return User.Role.ROLE_SUPER_ADMIN;
            case "VISITOR": return User.Role.ROLE_VISITOR;
            default: return User.Role.valueOf("ROLE_" + identifierToRole);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        return users.get(username);
    }
}
