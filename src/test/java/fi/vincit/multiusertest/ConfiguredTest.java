package fi.vincit.multiusertest;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;

public abstract class ConfiguredTest extends AbstractUserRoleIT<User, Long, User.Role> {
    @Override
    protected void loginWithUser(User user) {
    }

    @Override
    protected User createUser(String username, String firstName, String lastName, User.Role userRole, LoginRole loginRole) {
        return new User();
    }

    @Override
    protected User.Role stringToRole(String role) {
        return User.Role.valueOf(role);
    }

    @Override
    protected User getUserByUsername(String username) {
        return new User();
    }
}
