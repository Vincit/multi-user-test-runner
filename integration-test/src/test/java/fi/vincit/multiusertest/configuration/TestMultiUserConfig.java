package fi.vincit.multiusertest.configuration;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

public class TestMultiUserConfig extends AbstractMultiUserConfig<User, User.Role> {

    private static Map<String, User> users = new HashMap<>();

    public TestMultiUserConfig() {
    }

    private AuthorizationRule authorizationRule;

    @Override
    public void setAuthorizationRule(AuthorizationRule authorizationRule, Object testClassInstance) {
        this.authorizationRule = authorizationRule;
    }

    @Override
    protected AuthorizationRule getAuthorizationRule() {
        return authorizationRule;
    }

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
        return User.Role.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return users.get(username);
    }
}
