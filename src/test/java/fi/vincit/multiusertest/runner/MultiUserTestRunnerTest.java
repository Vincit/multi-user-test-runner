package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MultiUserTestRunnerTest {

    @Ignore
    public static class NoAnnotation {
    }

    private MultiUserTestClassRunnerFactory factory;

    @Before
    public void init() throws InitializationError {
        ParentRunner<FrameworkMethod> mockRunner = mock(ParentRunner.class);
        factory = mock(MultiUserTestClassRunnerFactory.class);
        when(factory.createTestRunner(any(Class.class), any(UserIdentifier.class), any(UserIdentifier.class)))
                .thenReturn(mockRunner);
    }

    @TestUsers(creators = "role:ROLE_USERS")
    @Ignore
    public static class OneCreator {
    }

    @TestUsers(creators = "role:ROLE_USERS", users = {"user:Foo", "role:Bar"})
    @Ignore
    public static class OneCreator_MultipleUsers {
    }

    @Test(expected = IllegalStateException.class)
    public void testClassWithoutTestUsersAnnotation() throws Throwable {
        createMultiUserTestRunner(NoAnnotation.class);
    }

    @Test
    public void testClassWith_OneCreator_DefaultUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneCreator.class);

        assertThat(runner.getChildren().size(), is(1));
        verify(factory).createTestRunner(
                any(Class.class),
                eq(UserIdentifier.parse("role:ROLE_USERS")),
                eq(UserIdentifier.getNewUser())
        );
    }

    @Test
    public void testClassWith_OneCreator_MultipleUsers() throws Throwable {
        MultiUserTestRunner runner = createMultiUserTestRunner(OneCreator_MultipleUsers.class);

        assertThat(runner.getChildren().size(), is(2));
        verify(factory).createTestRunner(
                any(Class.class),
                eq(UserIdentifier.parse("role:ROLE_USERS")),
                eq(UserIdentifier.parse("user:Foo"))
        );
        verify(factory).createTestRunner(
                any(Class.class),
                eq(UserIdentifier.parse("role:ROLE_USERS")),
                eq(UserIdentifier.parse("role:Bar"))
        );
    }

    private MultiUserTestRunner createMultiUserTestRunner(Class testClass) throws Throwable {
        return new MultiUserTestRunner(testClass, factory);
    }

}
