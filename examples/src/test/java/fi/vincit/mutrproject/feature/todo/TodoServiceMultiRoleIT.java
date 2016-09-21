package fi.vincit.mutrproject.feature.todo;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredMultiRoleIT;

/**
 * Example how to use multiple roles per user using intermediate role.
 * See {@link AbstractConfiguredMultiRoleIT} for an example how to implement multi role support.
 */
@RunWithUsers(
        producers = {"role:ADMINISTRATOR", "role:REGULAR_USER"},
        consumers = {"role:ADMINISTRATOR", "role:REGULAR_USER", RunWithUsers.PRODUCER}
)
public class TodoServiceMultiRoleIT extends AbstractConfiguredMultiRoleIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("role:REGULAR_USER")));
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
        authorization().expect(notToFail(ifAnyOf("role:ADMINISTRATOR", RunWithUsers.PRODUCER)));
        todoService.addItemToList(listId, "Write tests");
    }

}
