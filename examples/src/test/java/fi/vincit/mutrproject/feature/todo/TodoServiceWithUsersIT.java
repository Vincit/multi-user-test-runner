package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.feature.todo.dto.TodoItemDto;
import fi.vincit.mutrproject.feature.user.UserService;
import fi.vincit.mutrproject.feature.user.model.Role;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.assertValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectExceptionInsteadOfValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFail;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFailIgnoringValue;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Example how to use existing users
 */
@RunWithUsers(
        producers = {"user:admin", "user:user1"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "user:user2", RunWithUsers.PRODUCER}
)
public class TodoServiceWithUsersIT extends AbstractConfiguredMultiRoleIT {

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
        authorization().given(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf(users("user2"))
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        authorization().given(() -> todoService.getTodoList(id))
                .byDefault(assertValue(todoList -> {
                    assertThat(todoList.getId(), is(id));
                    assertThat(todoList.getName(), is("Test list"));
                }))
                .test();
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        authorization().given(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf(roles("ROLE_SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    @Test
    public void setTaskAsDone() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);

        authorization().given(() -> {
            long itemId = todoService.addItemToList(listId, "Write tests");
            TodoItemDto item = todoService.getTodoItem(listId, itemId);
            todoService.setItemStatus(listId, item.getId(), true);
        })
                .whenCalledWithAnyOf(roles("ROLE_SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFail())
                .otherwise(expectException(AccessDeniedException.class))
                .test();
    }

}
