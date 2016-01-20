package fi.vincit.multiusertest.context;

import fi.vincit.multiusertest.rule.AuthorizationRule;
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

    public TestMultiUserConfig(AuthorizationRule authorizationRule) {
        this.authorizationRule = authorizationRule;
    }

    private AuthorizationRule authorizationRule;

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationRule getAuthorizationRule() {
        return authorizationRule;
    }

    @Override
    public void setAuthorizationRule(AuthorizationRule authorizationRule, Object testClassInstance) {
        this.authorizationRule = authorizationRule;
        this.authorizationRule.setExpectedException(getDefaultException(testClassInstance.getClass()));
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
