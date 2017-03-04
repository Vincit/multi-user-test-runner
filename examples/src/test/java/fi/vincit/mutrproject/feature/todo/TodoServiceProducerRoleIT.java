package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static fi.vincit.multiusertest.rule.expectation2.TestExpectations.expectExceptionInsteadOfValue;
import static fi.vincit.multiusertest.rule.expectation2.TestExpectations.expectNotToFailIgnoringValue;

/**
 * Basic examples on how to use multi-user-test-runner with {@link RunWithUsers#WITH_PRODUCER_ROLE}.
 */
@RunWithUsers(
        producers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.WITH_PRODUCER_ROLE}
)
public class TodoServiceProducerRoleIT extends AbstractConfiguredMultiRoleIT {

    @Autowired
    private TodoService todoService;
    
    @Test
    public void getPrivateTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", false);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf("role:ROLE_USER")
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.getTodoList(id))
                .test();
    }

    @Test
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf("role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN")
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }


}
