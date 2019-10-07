package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.runner.junit5.JUnit5MultiUserTestRunner;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.configuration.TestMultiUserConfig;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.assertValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectExceptionInsteadOfValue;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFailIgnoringValue;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
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
@ExtendWith(SpringExtension.class)
@ExtendWith(JUnit5MultiUserTestRunner.class)
public class TodoServiceJUnit5IT {

    // Service under test
    @Autowired
    private TodoService todoService;

    @Autowired
    @MultiUserConfigClass
    private TestMultiUserConfig config;

    @TestTemplate
    public void getPrivateTodoList(Authorization authorization) throws Throwable {
        // At this point the producer has been logged in automatically
        long id = todoService.createTodoList("Test list", false);

        authorization.given(() -> todoService.getTodoList(id))
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

    @TestTemplate
    public void getPublicTodoList(Authorization authorization) throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        authorization.given(() -> todoService.getTodoList(id))
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
    @TestTemplate
    public void getPublicTodoListNotFailsExplicit(Authorization authorization) throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        authorization.given(() -> todoService.getTodoList(id))
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
    @TestTemplate
    public void getPublicTodoListNotFailsSimple(Authorization authorization) throws Throwable {
        long id = todoService.createTodoList("Test list", true);
        authorization.given(() -> todoService.getTodoList(id))
                .test();
    }

    /**
     * This test is run for all producers but ignored for the anonymous consumer
     * @throws Throwable
     */
    @TestTemplate
    @IgnoreForUsers(consumers = RunWithUsers.ANONYMOUS)
    public void addTodoItem(Authorization authorization) throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        authorization.given(() -> todoService.addItemToList(listId, "Write tests"))
                .whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

    /**
     * This test is run for all producers but only when the consumer is ANONYMOUS
     * @throws Throwable
     */
    @TestTemplate
    @RunWithUsers(consumers = RunWithUsers.ANONYMOUS)
    public void addTodoItemAnonymous(Authorization authorization) throws Throwable {
        long listId = todoService.createTodoList("Test list", false);
        authorization.given(() -> todoService.addItemToList(listId, "Write tests"))
                .byDefault(expectExceptionInsteadOfValue(AuthenticationCredentialsNotFoundException.class))
                .test();
    }

}
