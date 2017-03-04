package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import static fi.vincit.multiusertest.rule.expectation2.TestExpectations.assertValue;
import static fi.vincit.multiusertest.rule.expectation2.TestExpectations.expectExceptionInsteadOfValue;
import static fi.vincit.multiusertest.rule.expectation2.TestExpectations.expectNotToFailIgnoringValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Basic examples on how to use MUTR. This test demonstrates the usage of
 * roles and built-in user roles PRODUCER and ANONYMOUS.
 */
@RunWithUsers(
        producers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
public class TodoServiceIT extends AbstractConfiguredMultiRoleIT {

    // Service under test
    @Autowired
    private TodoService todoService;

    @Test
    public void getPrivateTodoList() throws Throwable {
        // At this point the producer has been logged in automatically
        long id = todoService.createTodoList("Test list", false);

        // Change user to consumer in order to test how getTodoList works
        config().logInAs(LoginRole.CONSUMER);

        authorization().testCall(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf("role:ROLE_USER", RunWithUsers.ANONYMOUS)
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .otherwise(assertValue(todoList -> {
                    assertThat(todoList, notNullValue());
                    assertThat(todoList.getId(), is(id));
                    assertThat(todoList.getName(), is("Test list"));
                    assertThat(todoList.isPublicList(), is(false));
                }))
                .test();
    }

    @Test
    public void getPublicTodoList() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.getTodoList(id))
                .otherwise(assertValue(todoList -> {
                    assertThat(todoList, notNullValue());
                    assertThat(todoList.getId(), is(id));
                    assertThat(todoList.getName(), is("Test list"));
                    assertThat(todoList.isPublicList(), is(true));
                }))
                .test();
    }

    /**
     * Example on how to only check that the method do test
     * that a method call doesn't fail. This version explicitly
     * shows the test writer/reader that it is not expected to fail
     * ever.
     * @throws Throwable
     */
    @Test
    public void getPublicTodoListNotFailsExplicit() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.getTodoList(id))
                .byDefault(expectNotToFailIgnoringValue())
                .test();
    }

    /**
     * Example on how to only check that the method do test
     * that a method call doesn't fail. This version uses the default
     * expectation and omits the <pre>.byDefault(expectNotToFailIgnoringValue())</pre>
     * call.
     * @throws Throwable
     */
    @Test
    public void getPublicTodoListNotFailsSimple() throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.getTodoList(id))
                .test();
    }

    /**
     * This test is run for all producers but ignored for the anonymous consumer
     * @throws Throwable
     */
    @Test
    @IgnoreForUsers(consumers = RunWithUsers.ANONYMOUS)
    public void addTodoItem() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf("role:ROLE_ADMIN", "role:ROLE_SYSTEM_ADMIN", RunWithUsers.PRODUCER)
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    /**
     * This test is run for all producers but only when the consumer is ANONYMOUS
     * @throws Throwable
     */
    @Test
    @RunWithUsers(consumers = RunWithUsers.ANONYMOUS)
    public void addTodoItemAnonymous() throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        config().logInAs(LoginRole.CONSUMER);
        authorization().testCall(() -> todoService.addItemToList(listId, "Write tests"))
                .byDefault(expectExceptionInsteadOfValue(AuthenticationCredentialsNotFoundException.class))
                .test();
    }

}
