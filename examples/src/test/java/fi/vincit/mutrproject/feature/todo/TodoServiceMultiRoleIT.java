package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestMultiRoleConfig;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

/**
 * Example how to use multiple roles per user using intermediate role.
 * See {@link AbstractConfiguredMultiRoleIT} for an example how to implement multi role support.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        defaultException = AccessDeniedException.class)
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
@RunWithUsers(
        producers = {"role:ADMINISTRATOR", "role:REGULAR_USER"},
        consumers = {"role:ADMINISTRATOR", "role:REGULAR_USER", RunWithUsers.PRODUCER}
)
public class TodoServiceMultiRoleIT {

    @Autowired
    private TodoService todoService;

    @Autowired
    private DatabaseUtil databaseUtil;

    @Autowired
    @MultiUserConfigClass
    public TestMultiRoleConfig config;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();


    @After
    public void tearDown() {
        databaseUtil.clearDb();
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        config.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:REGULAR_USER")));
        todoService.getTodoList(id);
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        config.logInAs(LoginRole.CONSUMER);
        todoService.getTodoList(id);
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        config.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(notToFail(ifAnyOf("role:ADMINISTRATOR", RunWithUsers.PRODUCER)));
        todoService.addItemToList(listId, "Write tests");
    }

}
