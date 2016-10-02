package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestConfig;
import fi.vincit.mutrproject.feature.todo.dto.TodoListDto;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.feature.user.model.User;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Arrays;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Examples how to use advanced assertions with Java 8
 */
@RunWithUsers(
        producers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
@RunWith(MultiUserTestRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@ContextConfiguration(classes = {Application.class, SecurityConfig.class, TestConfig.class})
@MultiUserTestConfig(defaultException = AccessDeniedException.class)
public class TodoServiceJava8IT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Autowired
    @MultiUserConfigClass
    protected MultiUserConfig<User, Role> multiUserConfig;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();


    @Autowired
    private TodoService todoService;

    @After
    public void clear() {
        databaseUtil.clearDb();
    }

    @Autowired
    private DatabaseUtil databaseUtil;

    @Before
    public void init() {
        todoService.clearList();
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(
                call(() -> todoService.getTodoList(id))
                        .toFail(ifAnyOf("role:ROLE_USER", RunWithUsers.ANONYMOUS))
        );
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        todoService.getTodoList(id);
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.expect(
                call(() -> todoService.addItemToList(listId, "Write tests"))
                        .toFail(ifAnyOf("role:ROLE_USER"))
                        .toFailWithException(AuthenticationCredentialsNotFoundException.class,
                                ifAnyOf(RunWithUsers.ANONYMOUS))
        );
    }

    @Test
    public void getListsCount() throws Throwable {
        todoService.createTodoList("Test list 1", false);
        todoService.createTodoList("Test list 2", true);
        todoService.createTodoList("Test list 3", false);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.expect(valueOf(() -> todoService.getTodoLists().size())
                        .toEqual(1, ifAnyOf("role:ROLE_USER", RunWithUsers.ANONYMOUS))
                        .toEqual(3, ifAnyOf(RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN"))
        );
    }

    @Test
    public void getLists() throws Throwable {
        todoService.createTodoList("Test list 1", false);
        todoService.createTodoList("Test list 2", true);
        todoService.createTodoList("Test list 3", false);

        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.expect(valueOf(() ->
                        todoService.getTodoLists().stream().map(TodoListDto::getName).collect(toList()))
                        .toAssert(value -> assertThat(value, is(Arrays.asList("Test list 2"))),
                                ifAnyOf("role:ROLE_USER")
                        )
                        .toAssert(value -> assertThat(value, is(Arrays.asList("Test list 1",
                                        "Test list 2",
                                        "Test list 3"))),
                                ifAnyOf(RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN")
                        )
        );
    }

}
