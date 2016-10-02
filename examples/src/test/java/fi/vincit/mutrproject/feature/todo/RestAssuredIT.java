package fi.vincit.mutrproject.feature.todo;

import com.jayway.restassured.response.Response;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestMultiUserRestConfig;
import fi.vincit.mutrproject.feature.todo.command.TodoItemCommand;
import fi.vincit.mutrproject.feature.todo.command.TodoListCommand;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.apache.http.HttpStatus;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import static fi.vincit.multiusertest.rule.expection.Expectations.valueOf;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Example of basic Spring project system integration tests
 * using MUTR and REST-assured.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig(
        defaultException = AccessDeniedException.class)
@SpringApplicationConfiguration(classes = {Application.class, SecurityConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
@RunWith(MultiUserTestRunner.class)
@RunWithUsers(
        producers = {"user:admin", "role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER", "user:user1",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
public class RestAssuredIT {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseUtil databaseUtil;

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();


    @Autowired
    @MultiUserConfigClass
    public TestMultiUserRestConfig config;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Before
    public void initUsers() {
        todoService.clearList();
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user1", "user1", Role.ROLE_USER);
    }

    @After
    public void tearDown() {
        userService.logout();
        databaseUtil.clearDb();
    }


    @Test
    public void getTodoLists() throws Throwable {
        config.whenAuthenticated()
                .body(new TodoListCommand("Test List 1", false)).post("/api/todo/list")
                .then().assertThat().statusCode(HttpStatus.SC_OK);
        config.whenAuthenticated()
                .body(new TodoListCommand("Test List 2", true)).post("/api/todo/list")
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        config.logInAs(LoginRole.CONSUMER);

        Response response = config.whenAuthenticated().get("/api/todo/lists");

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK)
                                .body("", hasSize(2))
                                .body("[0].name", equalTo("Test List 1"))
                                .body("[1].name", equalTo("Test List 2")),
                        ifAnyOf("role:ROLE_ADMIN", RunWithUsers.PRODUCER))
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK)
                                .body("", hasSize(1))
                                .body("[0].name", equalTo("Test List 2")),
                        ifAnyOf("role:ROLE_USER", "user:user1", RunWithUsers.ANONYMOUS)));
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        config.logInAs(LoginRole.CONSUMER);

        Response response = config.whenAuthenticated().get("/api/todo/list/" + id);

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK)
                                .assertThat().body("name", equalTo("Test List")),
                        ifAnyOf("role:ROLE_ADMIN", RunWithUsers.PRODUCER))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(RunWithUsers.ANONYMOUS)));
    }

    @Test
    public void addItemToPrivateList() throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        config.logInAs(LoginRole.CONSUMER);

        Response response = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item");

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", RunWithUsers.PRODUCER))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(RunWithUsers.ANONYMOUS)));
    }

    @Test
    public void addItemToPublicList() throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        config.logInAs(LoginRole.CONSUMER);

        Response response = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item");

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", RunWithUsers.PRODUCER))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(RunWithUsers.ANONYMOUS)));
    }

    @Test
    public void setPrivateItemAsDone() throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", false)).post("/api/todo/list")
                .body().as(Long.class);

        long itemId = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item")
                .body().as(Long.class);

        config.logInAs(LoginRole.CONSUMER);

        Response response = config.whenAuthenticated().post(String.format("/api/todo/list/%s/%s/done", listId, itemId));

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", RunWithUsers.PRODUCER))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(RunWithUsers.ANONYMOUS)));
    }

    @Test
    public void setPublicItemAsDone() throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", true)).post("/api/todo/list")
                .body().as(Long.class);

        long itemId = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List")).post("/api/todo/list/item")
                .body().as(Long.class);

        config.logInAs(LoginRole.CONSUMER);

        Response response = config.whenAuthenticated().post(String.format("/api/todo/list/%s/%s/done", listId, itemId));

        authorizationRule.expect(valueOf(response::then)
                .toAssert(t -> t.statusCode(HttpStatus.SC_OK),
                        ifAnyOf("role:ROLE_ADMIN", RunWithUsers.PRODUCER))
                .toAssert(t -> t.statusCode(HttpStatus.SC_FORBIDDEN),
                        ifAnyOf("role:ROLE_USER", "user:user1"))
                .toAssert(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED),
                        ifAnyOf(RunWithUsers.ANONYMOUS)));

    }

}
