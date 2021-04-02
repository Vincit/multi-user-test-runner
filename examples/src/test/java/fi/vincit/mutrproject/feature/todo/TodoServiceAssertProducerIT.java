package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;
import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.*;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;

/**
 * Basic examples on how to use MUTR. This test demonstrates the usage of
 * roles and built-in user roles PRODUCER and ANONYMOUS.
 */
@RunWithUsers(
        producers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS}
)
public class TodoServiceAssertProducerIT extends AbstractConfiguredMultiRoleIT {

    // Service under test
    @Autowired
    private TodoService todoService;

    /**
     * This test is run for all producers but ignored for the anonymous consumer
     * @throws Throwable
     */
    @Test
    public void addTodoItem() throws Throwable {
        // Editing of system admin todos is only allowed by another system admin or the owner of the task
        todoService.setSecureSystemAdminTodos(true);

        long listId = todoService.createTodoList("Test list", false);
        authorization().given(() -> todoService.addItemToList(listId, "Write tests"))
                .whenProducerIsAnyOf(roles("ROLE_SYSTEM_ADMIN"))
                .whenCalledWithAnyOf(roles("ROLE_SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_USER"))
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))

                .whenProducerIsAnyOf(roles("ROLE_ADMIN", "ROLE_USER"))
                .whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_SYSTEM_ADMIN"), UserIdentifiers.producer())
                .then(expectNotToFailIgnoringValue())
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectExceptionInsteadOfValue(AccessDeniedException.class))

                .whenProducerIsAny()
                .whenCalledWithAnyOf(UserIdentifier.getAnonymous())
                .then(expectExceptionInsteadOfValue(AuthenticationCredentialsNotFoundException.class))

                .otherwise(expectExceptionInsteadOfValue(AccessDeniedException.class))
                .test();
    }

}
