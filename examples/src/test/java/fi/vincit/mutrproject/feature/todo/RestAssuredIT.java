package fi.vincit.mutrproject.feature.todo;

import com.jayway.restassured.response.Response;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.runner.junit5.JUnit5MultiUserTestRunner;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.Application;
import fi.vincit.mutrproject.config.SecurityConfig;
import fi.vincit.mutrproject.configuration.TestMultiUserRestConfig;
import fi.vincit.mutrproject.feature.todo.command.ListVisibility;
import fi.vincit.mutrproject.feature.todo.command.TodoItemCommand;
import fi.vincit.mutrproject.feature.todo.command.TodoListCommand;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.util.DatabaseUtil;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.assertResponse;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * Example of basic Spring project system integration tests
 * using MUTR expectation syntax and REST-assured.
 */
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@MultiUserTestConfig
@ContextConfiguration(classes = {Application.class, SecurityConfig.class})
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWithUsers(
        producers = {"user:admin", "role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER", "user:user1",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
@ExtendWith(SpringExtension.class)
@ExtendWith(JUnit5MultiUserTestRunner.class)
public class RestAssuredIT {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseUtil databaseUtil;


    @Autowired
    @MultiUserConfigClass
    public TestMultiUserRestConfig config;

    @Before
    public void initUsers() {
        todoService.clearList();
        todoService.setSecureSystemAdminTodos(false);
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user1", "user1", Role.ROLE_USER);
    }

    @After
    public void tearDown() {
        userService.logout();
        databaseUtil.clearDb();
    }


    @TestTemplate
    public void getTodoLists(Authorization authorization) throws Throwable {
        config.whenAuthenticated()
                .body(new TodoListCommand("Test List 1", ListVisibility.PRIVATE))
                .post("/api/todo/list")
                .then().assertThat().statusCode(HttpStatus.SC_OK);
        config.whenAuthenticated()
                .body(new TodoListCommand("Test List 2", ListVisibility.PUBLIC))
                .post("/api/todo/list")
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        Response response = config.whenAuthenticated().get("/api/todo/lists");

        authorization.given(response::then)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), UserIdentifiers.producer())
                .then(assertResponse(t -> t
                        .statusCode(HttpStatus.SC_OK)
                        .body("", hasSize(2))
                        .body("[0].name", equalTo("Test List 1"))
                        .body("[1].name", equalTo("Test List 2"))))
                .whenCalledWithAnyOf(roles("ROLE_USER"), users("user1"), UserIdentifiers.anonymous())
                .then(assertResponse(t -> t
                        .statusCode(HttpStatus.SC_OK)
                        .body("", hasSize(1))
                        .body("[0].name", equalTo("Test List 2"))))
                .test();
    }

    @TestTemplate
    public void getPrivateTodoList(Authorization authorization) throws Throwable {
        long id = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", ListVisibility.PRIVATE))
                .post("/api/todo/list")
                .body().as(Long.class);

        Response response = config.whenAuthenticated().get("/api/todo/list/" + id);

        authorization.given(response::then)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), UserIdentifiers.producer())
                .then(assertResponse(t -> t
                        .statusCode(HttpStatus.SC_OK)
                        .body("name", equalTo("Test List")))
                ).whenCalledWithAnyOf(roles("ROLE_USER"), users("user1"))
                .then(assertResponse(t -> t
                        .statusCode(HttpStatus.SC_FORBIDDEN))
                ).whenCalledWithAnyOf(UserIdentifiers.anonymous())
                .then(assertResponse(t -> t
                        .statusCode(HttpStatus.SC_UNAUTHORIZED))
                ).test();
    }

    @TestTemplate
    public void addItemToPrivateList(Authorization authorization) throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", ListVisibility.PRIVATE))
                .post("/api/todo/list")
                .body().as(Long.class);

        Response response = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List"))
                .post("/api/todo/list/item");

        authorization.given(response::then)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), UserIdentifiers.producer())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_OK)))
                .whenCalledWithAnyOf(roles("ROLE_USER"), users("user1"))
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_FORBIDDEN)))
                .whenCalledWithAnyOf(UserIdentifiers.anonymous())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED)))
                .test();
    }

    @TestTemplate
    public void addItemToPublicList(Authorization authorization) throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", ListVisibility.PRIVATE))
                .post("/api/todo/list")
                .body().as(Long.class);

        Response response = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List"))
                .post("/api/todo/list/item");

        authorization.given(response::then)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), UserIdentifiers.producer())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_OK)))
                .whenCalledWithAnyOf(roles("ROLE_USER"), users("user1"))
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_FORBIDDEN)))
                .whenCalledWithAnyOf(UserIdentifiers.anonymous())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED)))
                .test();
    }

    @TestTemplate
    public void setPrivateItemAsDone(Authorization authorization) throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", ListVisibility.PRIVATE))
                .post("/api/todo/list")
                .body().as(Long.class);

        long itemId = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List"))
                .post("/api/todo/list/item")
                .body().as(Long.class);

        Response response = config.whenAuthenticated().post(String.format("/api/todo/list/%d/%d/done", listId, itemId));

        authorization.given(response::then)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), UserIdentifiers.producer())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_OK)))
                .whenCalledWithAnyOf(roles("ROLE_USER"), users("user1"))
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_FORBIDDEN)))
                .whenCalledWithAnyOf(UserIdentifiers.anonymous())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED)))
                .test();
    }

    @TestTemplate
    public void setPublicItemAsDone(Authorization authorization) throws Throwable {
        long listId = config.whenAuthenticated()
                .body(new TodoListCommand("Test List", ListVisibility.PUBLIC))
                .post("/api/todo/list")
                .body().as(Long.class);

        long itemId = config.whenAuthenticated()
                .body(new TodoItemCommand(listId, "Test List"))
                .post("/api/todo/list/item")
                .body().as(Long.class);

        Response response = config.whenAuthenticated().post(String.format("/api/todo/list/%d/%d/done", listId, itemId));

        authorization.given(response::then)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), UserIdentifiers.producer())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_OK)))
                .whenCalledWithAnyOf(roles("ROLE_USER"), users("user1"))
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_FORBIDDEN)))
                .whenCalledWithAnyOf(UserIdentifiers.anonymous())
                .then(assertResponse(t -> t.statusCode(HttpStatus.SC_UNAUTHORIZED)))
                .test();
    }

}
