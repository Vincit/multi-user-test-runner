package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.test.AbstractMultiUserConfig;

import java.util.HashMap;
import java.util.Map;

public class InitProducerBeforeTestConfiguredTest extends AbstractMultiUserConfig<User, User.Role> {

    private static Map<String, User> users = new HashMap<>();

    private static boolean producerCreated = false;

    public static void setProducerCreated(boolean producerCreated) {
        InitProducerBeforeTestConfiguredTest.producerCreated = producerCreated;
    }

    @Override
    public void loginWithUser(User user) {
        SecurityUtil.logInUser(user);
    }

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
    public void logInAs(LoginRole role) {
        if (role == LoginRole.PRODUCER) {
            if (!producerCreated) {
                throw new AssertionError("No produced created before logInAs call");
            }
        } else {
            super.logInAs(role);
        }
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
