package fi.vincit.mutrproject.configuration;

import com.jayway.restassured.specification.RequestSpecification;
import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jayway.restassured.RestAssured.given;

/**
 * Example of basic Spring project system integration tests
 * using MUTR and REST-assured.
 */
public class TestMultiUserRestConfig extends AbstractMultiUserConfig<User, Role> {

    @Autowired
    private UserService userService;

    private String username;
    private String password;
    private boolean isAnonymous;

    @Override
    public void loginWithUser(User user) {
        username = user.getUsername();
        password = user.getUsername();
        isAnonymous = false;
    }

    @Override
    public void loginAnonymous() {
        username = null;
        password = null;
        isAnonymous = true;
    }

    /**
     * Helper method for authenticating via REST
     * @return REST-assured RequestSpecification
     */
    public RequestSpecification whenAuthenticated() {
        RequestSpecification spec = given();
        if (!isAnonymous) {
            spec = spec.auth().preemptive().basic(username, password);
        }
        return spec.header("Content-Type", "application/json");
    }

    @Override
    public User createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
        final String password = username;
        return userService.createUser(username, password, userRole);
    }

    @Override
    public Role stringToRole(String role) {
        return Role.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
