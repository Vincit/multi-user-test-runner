package fi.vincit.multiusertest.runner;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestMethodFilter;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RunnerDelegateTest {

    @Test
    public void testGetName() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        assertThat(
                delegate.getName(new TestClass(Object.class)),
                is("java.lang.Object: creator = role:ROLE_ADMIN; user = role:ROLE_USER")
        );
    }

    @Test
    public void getName() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        assertThat(
                delegate.testName(mockFrameworkMethod()),
                is("testMethod: creator = role:ROLE_ADMIN; user = role:ROLE_USER")
        );
    }

    @Ignore
    private static class TestConfig extends AbstractUserRoleIT<String, String> {
        @Override
        protected void loginWithUser(String s) {
        }

        @Override
        protected String createUser(String username, String firstName, String lastName, String userRole, LoginRole loginRole) {
            return null;
        }

        @Override
        protected String stringToRole(String role) {
            return null;
        }

        @Override
        protected String getUserByUsername(String username) {
            return null;
        }

        public TestUser<String, String> getUserModel() {
            return super.getUserModel();
        }

        public TestUser<String, String> getCreatorModel() {
            return super.getCreatorModel();
        }
    }

    @Test
    public void testCreateTest() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );
        TestConfig instance = (TestConfig) delegate.createTest(new TestConfig());

        assertThat(instance, notNullValue());
        assertThat(instance.getCreatorModel(), notNullValue());
        assertThat(instance.getCreatorModel().getIdentifier(), is("ROLE_ADMIN"));

        assertThat(instance.getUserModel(), notNullValue());
        assertThat(instance.getUserModel().getIdentifier(), is("ROLE_USER"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateTestWithInvalidClassType() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        delegate.createTest(new Object());
    }

    @Test
    public void testIsIgnoredByChild() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(false);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), false), is(true));
    }

    private FrameworkMethod mockFrameworkMethod() {
        FrameworkMethod frameworkMethod = mock(FrameworkMethod.class);
        when(frameworkMethod.getName()).thenReturn("testMethod");
        return frameworkMethod;
    }

    @Test
    public void testIsIgnoredWhenParentIsIgnored() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(true);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), true), is(true));
    }

    @Test
    public void testIsIgnoredWhenParentAndChildAreIgnored() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(false);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), true), is(true));
    }

    @Test
    public void testIsNotIgnored() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(true);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), false), is(false));
    }

    @Test
    public void testFilter() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        List<FrameworkMethod> methods = Arrays.asList(mockFrameworkMethod(), mockFrameworkMethod());
        when(testMethodFilter.filter(any(List.class))).thenReturn(methods);

        List<FrameworkMethod> inputMethods = Arrays.asList(mockFrameworkMethod(), mockFrameworkMethod(), mockFrameworkMethod());
        assertThat(delegate.filterMethods(inputMethods), is(methods));
    }

    @Test
    public void testFilterWhenNothingToRun() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        List<FrameworkMethod> methods = Collections.emptyList();
        when(testMethodFilter.filter(any(List.class))).thenReturn(methods);

        List<FrameworkMethod> inputMethods = Arrays.asList(mockFrameworkMethod(), mockFrameworkMethod(), mockFrameworkMethod());
        assertThat(delegate.filterMethods(inputMethods), is(inputMethods));
    }
}