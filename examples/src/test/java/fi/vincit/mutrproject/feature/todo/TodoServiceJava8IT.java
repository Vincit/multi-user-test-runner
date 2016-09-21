package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;
import fi.vincit.mutrproject.feature.todo.dto.TodoListDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

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
public class TodoServiceJava8IT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Before
    public void init() {
        todoService.clearList();
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(
                call(() -> todoService.getTodoList(id))
                        .toFail(ifAnyOf("role:ROLE_USER", RunWithUsers.ANONYMOUS))
        );
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        logInAs(LoginRole.CONSUMER);
        todoService.getTodoList(id);
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);

        authorization().expect(
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

        logInAs(LoginRole.CONSUMER);

        authorization().expect(valueOf(() -> todoService.getTodoLists().size())
                        .toEqual(1, ifAnyOf("role:ROLE_USER", RunWithUsers.ANONYMOUS))
                        .toEqual(3, ifAnyOf(RunWithUsers.PRODUCER, "role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN"))
        );
    }

    @Test
    public void getLists() throws Throwable {
        todoService.createTodoList("Test list 1", false);
        todoService.createTodoList("Test list 2", true);
        todoService.createTodoList("Test list 3", false);

        logInAs(LoginRole.CONSUMER);

        authorization().expect(valueOf(() ->
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
