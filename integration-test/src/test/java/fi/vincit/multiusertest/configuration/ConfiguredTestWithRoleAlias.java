package fi.vincit.multiusertest.configuration;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;

@MultiUserTestConfig(runner = BlockMultiUserTestClassRunner.class)
public abstract class ConfiguredTestWithRoleAlias extends AbstractUserRoleIT<User, User.Role> {

    private static Map<String, User> users = new HashMap<>();

    @Override
    protected void loginWithUser(User user) {
        SecurityUtil.logInUser(user);
    }

    @After
    public void tearDown() {
        SecurityUtil.clear();
    }

    @Override
    protected User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        User user = new User(username, userRole);
        users.put(username, user);
        return user;
    }

    @Override
    protected User.Role stringToRole(String role) {
        switch(role) {
            case "NORMAL": return User.Role.ROLE_USER;
            case "UNREGISTERED": return User.Role.ROLE_VISITOR;
            default: return User.Role.valueOf("ROLE_" + role);
        }
    }

    @Override
    protected User getUserByUsername(String username) {
        return users.get(username);
    }
}
