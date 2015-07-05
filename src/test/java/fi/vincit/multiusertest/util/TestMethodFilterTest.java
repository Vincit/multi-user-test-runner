package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.TestUsers;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMethodFilterTest {

    @Test
    public void testRunMetdhodWhenCreatorAndUserSet() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[]{"role:ROLE_ADMIN"}, new String[]{"role:ROLE_ADMIN"});
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testDontRunMethodWhenUserDoesntMatch() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[] {"role:ROLE_ADMIN"}, new String[] {"role:ROLE_ADMIN"});
        assertThat(r.shouldRun(method), is(false));
    }

    @Test
    public void testDontRunMethodWhenCreatorDoesntMatch() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[] {"role:ROLE_ADMIN"}, new String[] {"role:ROLE_ADMIN"});
        assertThat(r.shouldRun(method), is(false));
    }

    @Test
    public void testRunMethodWhenMultipleUserRoles() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"));

        FrameworkMethod method = mock(FrameworkMethod.class);

        mockTestUsers(method,
                new String[]{"role:ROLE_ADMIN"},
                new String[]{"role:ROLE_ADMIN", "role:ROLE_USER"}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenMultipleCreatorRoles() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{"role:ROLE_ADMIN", "role:ROLE_USER"},
                new String[]{"role:ROLE_ADMIN"}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoUserDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{"role:ROLE_USER"},
                new String[]{}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoCreatorDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{},
                new String[]{"role:ROLE_ADMIN"}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoRolesDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{},
                new String[]{}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoAnnotationDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        assertThat(r.shouldRun(method), is(true));
    }

    private void mockTestUsers(FrameworkMethod method, String[] creators, String[] users) {
        TestUsers testUsers = mock(TestUsers.class);

        when(testUsers.creators()).thenReturn(creators);
        when(testUsers.users()).thenReturn(users);

        when(method.getAnnotation(TestUsers.class)).thenReturn(testUsers);
    }
}