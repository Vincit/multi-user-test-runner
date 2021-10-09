package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestMultiRoleConfig;
import fi.vincit.mutrproject.feature.todo.command.ListVisibility;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.junit.*;
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

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectExceptionInsteadOfValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFailIgnoringValue;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;

/**
 * Example how to use multiple roles per user using intermediate role.
 * See {@link AbstractConfiguredMultiRoleIT} for an example how to implement multi role support.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
@RunWithUsers(
        producers = {"role:ADMIN:USER", "role:USER"},
        consumers = {"role:ADMIN:USER", "role:USER", RunWithUsers.PRODUCER}
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

    @Before
    public void init() {
        todoService.setSecureSystemAdminTodos(false);
    }

    @After
    public void tearDown() {
        databaseUtil.clearDb();
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", ListVisibility.PRIVATE);
        authorizationRule.given(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf(roles("USER"))
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", ListVisibility.PUBLIC);
        authorizationRule.given(() -> todoService.getTodoList(id))
                .byDefault(expectNotToFailIgnoringValue())
                .test();
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", ListVisibility.PRIVATE);
        authorizationRule.given(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf(roles("ADMIN:USER"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

}
