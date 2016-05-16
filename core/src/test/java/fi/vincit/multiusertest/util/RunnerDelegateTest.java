package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.test.AbstractMultiUserConfig;
import fi.vincit.multiusertest.test.UserRoleIT;
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
                is("producer={role:ROLE_ADMIN}, consumer={role:ROLE_USER}")
        );
    }

    @Test
    public void testName() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        assertThat(
                delegate.testName(mockFrameworkMethod()),
                is("testMethod")
        );
    }

    @Ignore
    private static class MissingTestConfig extends AbstractMultiUserConfig<String, String> {

        @Override
        public void loginWithUser(String s) {
        }

        @Override
        public String createUser(String username, String firstName, String lastName, String userRole, LoginRole loginRole) {
            return null;
        }

        @Override
        public String stringToRole(String role) {
            return null;
        }

        @Override
        public String getUserByUsername(String username) {
            return null;
        }

        @Override
        public RoleContainer<String> getConsumerRoleContainer() {
            return super.getConsumerRoleContainer();
        }

        @Override
        public RoleContainer<String> getProducerRoleContainer() {
            return super.getProducerRoleContainer();
        }
    }

    @Ignore
    private static class TestConfig extends AbstractMultiUserConfig<String, String> {

        public TestConfig(UserRoleIT<String> config) {
            this.config = config;
        }

        @MultiUserConfigClass
        public final UserRoleIT<String> config;

        @Override
        public void loginWithUser(String s) {
        }

        @Override
        public String createUser(String username, String firstName, String lastName, String userRole, LoginRole loginRole) {
            return null;
        }

        @Override
        public String stringToRole(String role) {
            return null;
        }

        @Override
        public String getUserByUsername(String username) {
            return null;
        }

        @Override
        public RoleContainer<String> getConsumerRoleContainer() {
            return super.getConsumerRoleContainer();
        }

        @Override
        public RoleContainer<String> getProducerRoleContainer() {
            return super.getProducerRoleContainer();
        }
    }

    @Ignore
    private static class InheritedTestConfig extends TestConfig {
        public InheritedTestConfig(UserRoleIT<String> config) {
            super(config);
        }
    }

    @Test
    public void testValidateTestInstance() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );
        UserRoleIT<String> mockConfig = mock(UserRoleIT.class);
        TestConfig instance = (TestConfig) delegate.validateTestInstance(new TestConfig(mockConfig));

        assertThat(instance, notNullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateTestInstanceWithInvalidClassType() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        delegate.validateTestInstance(new Object());
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateTestInstanceWithMissingConfigClass() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );
        delegate.validateTestInstance(new MissingTestConfig());
    }

    @Test
    public void testValidateInheritedTestInstance() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );
        UserRoleIT<String> mockConfig = mock(UserRoleIT.class);
        TestConfig instance = (InheritedTestConfig) delegate.validateTestInstance(new InheritedTestConfig(mockConfig));

        assertThat(instance, notNullValue());
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