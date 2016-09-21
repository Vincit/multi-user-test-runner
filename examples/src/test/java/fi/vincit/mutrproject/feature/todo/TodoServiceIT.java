package fi.vincit.mutrproject.feature.todo;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.configuration.AbstractConfiguredIT;

/**
 * Basic examples on how to use multi-user-test-runner.
 */
@RunWithUsers(
        producers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
public class TodoServiceIT extends AbstractConfiguredIT {

    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(toFail(ifAnyOf("role:ROLE_USER", RunWithUsers.ANONYMOUS)));
        todoService.getTodoList(id);
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        logInAs(LoginRole.CONSUMER);
        todoService.getTodoList(id);
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", RunWithUsers.PRODUCER})
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        authorization().expect(notToFail(ifAnyOf("role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN", RunWithUsers.PRODUCER)));
        todoService.addItemToList(listId, "Write tests");
    }

    /**
     * See {@link TodoServiceJava8IT} for nicer version
     * @throws Throwable
     */
    @Test(expected = AuthenticationCredentialsNotFoundException.class)
    @RunWithUsers(consumers = RunWithUsers.ANONYMOUS)
    public void addTodoItemAnonymous() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        logInAs(LoginRole.CONSUMER);
        todoService.addItemToList(listId, "Write tests");
    }

}
