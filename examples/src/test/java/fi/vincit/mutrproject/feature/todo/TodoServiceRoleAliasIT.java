package fi.vincit.mutrproject.feature.todo;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredRoleAliasIT;

/**
 * Example test using role aliasing. See {@link AbstractConfiguredRoleAliasIT} for an example
 * how to implement role aliasing.
 */
@RunWithUsers(
        producers = {"role:SYSTEM_ADMIN", "role:ADMIN", "role:REGULAR"},
        consumers = {"role:SYSTEM_ADMIN", "role:ADMIN", "role:REGULAR", RunWithUsers.PRODUCER}
)
public class TodoServiceRoleAliasIT extends AbstractConfiguredRoleAliasIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("role:REGULAR")));
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
        authorization().expect(notToFail(ifAnyOf("role:ADMIN", "role:SYSTEM_ADMIN", RunWithUsers.PRODUCER)));
        todoService.addItemToList(listId, "Write tests");
    }

}
