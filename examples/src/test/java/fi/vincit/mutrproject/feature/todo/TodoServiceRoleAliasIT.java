package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestMultiUserAliasConfig;
import fi.vincit.mutrproject.feature.todo.command.ListVisibility;
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
 * Example test using role aliasing. See {@link TestMultiUserAliasConfig} for an example
 * how to implement role aliasing.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@RunWith(MultiUserTestRunner.class)
@RunWithUsers(
        producers = {"role:SYSTEM_ADMIN", "role:ADMIN", "role:REGULAR"},
        consumers = {"role:SYSTEM_ADMIN", "role:ADMIN", "role:REGULAR", RunWithUsers.PRODUCER}
)
public class TodoServiceRoleAliasIT {

    @Autowired
    private TodoService todoService;

    @Autowired
    private DatabaseUtil databaseUtil;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();


    @Autowired
    @MultiUserConfigClass
    public TestMultiUserAliasConfig config;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Before
    public void init() {
        todoService.setSecureSystemAdminTodos(false);
    }

    @After
    public void clear() {
        databaseUtil.clearDb();
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", ListVisibility.PRIVATE);
        authorizationRule.given(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf(roles("REGULAR"))
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", ListVisibility.PUBLIC);
        authorizationRule.given(() -> todoService.getTodoList(id))
                .test();
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", ListVisibility.PRIVATE);
        authorizationRule.given(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf(roles("ADMIN", "SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }


}
