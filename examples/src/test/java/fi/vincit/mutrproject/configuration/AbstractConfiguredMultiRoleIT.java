package fi.vincit.mutrproject.configuration;


import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

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
 * Example on how to configure users with multiple roles. Uses a custom role
 * {@link RoleGroup} to configure which roles to configure for the user. RoleGroup
 * can be any enum or object that just defines all the combinations that need to
 * be tested.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        runner = SpringMultiUserTestClassRunner.class,
        defaultException = AccessDeniedException.class)
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
public abstract class AbstractConfiguredMultiRoleIT extends AbstractUserRoleIT<User, RoleGroup> {

    @After
    public void clear() {
        databaseUtil.clearDb();
    }

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    private UserService userService;

    @Override
    public void loginWithUser(User user) {
        userService.loginUser(user);
    }

    @Override
    public User createUser(String username, String firstName, String lastName, RoleGroup userRole, LoginRole loginRole) {
        return userService.createUser(username, username, roleGroupToRoles(userRole));
    }

    private Collection<Role> roleGroupToRoles(RoleGroup roleGroup) {
        switch (roleGroup) {
            case ADMINISTRATOR: return Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_MODERATOR, Role.ROLE_USER);
            case REGULAR_USER: return Arrays.asList(Role.ROLE_USER);
            default: throw new IllegalArgumentException("Invalid role group " + roleGroup);
        }
    }

    @Override
    public RoleGroup stringToRole(String role) {
        return RoleGroup.valueOf(role);
    }

    @Override
    public User getUserByUsername(String username) {
        return userService.loadUserByUsername(username);
    }
}
