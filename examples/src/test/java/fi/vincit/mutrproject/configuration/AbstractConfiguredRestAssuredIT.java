package fi.vincit.mutrproject.configuration;


import static com.jayway.restassured.RestAssured.given;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.SpringMultiUserTestClassRunner;
import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import fi.vincit.mutrproject.util.DatabaseUtil;

/**
 * Example of basic configuration for Spring projects.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class,
        defaultException = AccessDeniedException.class)
@SpringApplicationConfiguration(classes = {Application.class, SecurityConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractConfiguredRestAssuredIT extends AbstractUserRoleIT<User, Role> {

    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
    }

    @After
    public void clear() {
        databaseUtil.clearDb();
    }

    @Autowired
    private DatabaseUtil databaseUtil;

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

    protected RequestSpecification whenAuthenticated() {
        RequestSpecification spec = given();
        if (!isAnonymous) {
            spec = spec.auth().preemptive().basic(username, password);
        } else {
            spec = spec;
        }
        return spec.header("Content-Type", "application/json");
    }

    @Override
    public User createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
        String password = username;
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
