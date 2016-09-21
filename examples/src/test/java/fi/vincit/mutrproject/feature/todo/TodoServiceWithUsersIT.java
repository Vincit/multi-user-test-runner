package fi.vincit.mutrproject.feature.todo;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;
import fi.vincit.mutrproject.feature.todo.dto.TodoItemDto;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;

/**
 * Example how to use existing users
 */
@RunWithUsers(
        producers = {"user:admin", "user:user1"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "user:user2", RunWithUsers.PRODUCER}
)
public class TodoServiceWithUsersIT extends AbstractConfiguredIT {

    @After
    public void clear() {
        todoService.clearList();
        userService.clearUsers();
    }

    @Autowired
    private UserService userService;
    @Autowired
    private TodoService todoService;

    @Before
    public void initUsers() {
        userService.createUser("admin", "admin", Role.ROLE_ADMIN);
        userService.createUser("user1", "user1", Role.ROLE_USER);
        userService.createUser("user2", "user2", Role.ROLE_USER);
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("user:user2", "role:ROLE_ANONYMOUS")));
        todoService.getTodoList(id);
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
        authorization().expect(notToFail(ifAnyOf("role:ROLE_SYSTEM_ADMIN", RunWithUsers.PRODUCER)));
        todoService.addItemToList(listId, "Write tests");
    }

    @Test
    public void setTaskAsDone() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);

        logInAs(LoginRole.CONSUMER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_SYSTEM_ADMIN", RunWithUsers.PRODUCER)));
        long itemId = todoService.addItemToList(listId, "Write tests");
        TodoItemDto item = todoService.getTodoItem(listId, itemId);
        todoService.setItemStatus(listId, item.getId(), true);
    }

}
