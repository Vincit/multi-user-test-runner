package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.assertValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectExceptionInsteadOfValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFailIgnoringValue;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Basic examples on how to use MUTR. This test demonstrates the usage of
 * roles via user definition classes.
 */
@RunWithUsers(
        producerClass = TodoProducers.class,
        consumerClass = TodoConsumers.class
)
public class TodoServiceUserDefinitionsIT extends AbstractConfiguredMultiRoleIT {

    // Service under test
    @Autowired
    private TodoService todoService;

    @Before
    public void init() {
        todoService.setSecureSystemAdminTodos(false);
    }

    @Test
    public void getPrivateTodoList() throws Throwable {
        // At this point the producer has been logged in automatically
        long id = todoService.createTodoList("Test list", false);

        authorization().given(() -> todoService.getTodoList(id))
                .whenCalledWithAnyOf(roles("ROLE_USER"), UserIdentifiers.anonymous())
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
        authorization().given(() -> todoService.getTodoList(id))
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
        authorization().given(() -> todoService.getTodoList(id))
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
        authorization().given(() -> todoService.getTodoList(id))
                .test();
    }

    /**
     * This test is run for all producers but ignored for the anonymous consumer
     * @throws Throwable
     */
    @Test
    @IgnoreForUsers(consumerClass = TodoAnonymous.class)
    public void addTodoItem() throws Throwable {

        assertThat(config.getConsumer(), notNullValue());

        long listId = todoService.createTodoList("Test list", false);
        authorization().given(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    /**
     * This test is run for all producers but only when the consumer is ANONYMOUS
     * @throws Throwable
     */
    @Test
    @RunWithUsers(consumerClass = TodoAnonymous.class)
    public void addTodoItemAnonymous() throws Throwable {

        assertThat(config.getConsumer(), nullValue());

        long listId = todoService.createTodoList("Test list", false);
        authorization().given(() -> todoService.addItemToList(listId, "Write tests"))
                .byDefault(expectExceptionInsteadOfValue(AuthenticationCredentialsNotFoundException.class))
                .test();
    }

}
