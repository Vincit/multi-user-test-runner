package fi.vincit.multiusertest.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;

public class AbstractUserRoleITTest {

    public enum Role {
        ROLE1,
        ROLE2
    }

    @RunWithUsers(producers = "role:ROLE1", consumers = "role:ROLE1")
    @MultiUserTestConfig(runner = BlockMultiUserTestClassRunner.class)
    public static class TestClass extends AbstractUserRoleIT<String, Role> {
        @Override
        public void loginWithUser(String user) {

        }

        @Override
        public void loginAnonymous() {
            super.loginAnonymous();
        }

        @Override
        public String createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
            return username;
        }

        @Override
        public Role stringToRole(String role) {
            return Role.valueOf(role);
        }

        @Override
        public String getUserByUsername(String username) {
            return null;
        }

        @Test
        public void mockTestMethod() {
        }
    }

    @Test
    public void testUserWithProducerRole() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getWithProducerRole());
        spyClass.initializeUsers();

        verify(spyClass).createUser(anyString(), eq("Test"), eq("Producer"), eq(Role.ROLE1), eq(LoginRole.PRODUCER));
        verify(spyClass).createUser(anyString(), eq("Test"), eq("Consumer"), eq(Role.ROLE1), eq(LoginRole.CONSUMER));

        spyClass.logInAs(LoginRole.CONSUMER);

        assertThat(spyClass.getConsumer(), notNullValue());
        assertThat(spyClass.getConsumer(), is(not((spyClass.getProducer()))));
        verify(spyClass.authorizationRule).setRole(new UserIdentifier(UserIdentifier.Type.ROLE, "ROLE1"));
    }

    @Test
    public void testLoginWithUserWithProducerRole() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getWithProducerRole());
        spyClass.initializeUsers();

        spyClass.logInAs(LoginRole.CONSUMER);

        verify(spyClass.authorizationRule).setRole(new UserIdentifier(UserIdentifier.Type.ROLE, "ROLE1"));
    }

    @Test
    public void testProducerRole() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getProducer());
        spyClass.initializeUsers();

        verify(spyClass).createUser(anyString(), eq("Test"), eq("Producer"), eq(Role.ROLE1), eq(LoginRole.PRODUCER));
        assertThat(spyClass.getConsumer(), notNullValue());
        assertThat(spyClass.getConsumer(), is(spyClass.getProducer()));
    }

    @Test
    public void testLoginWithProducerRole() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getProducer());
        spyClass.initializeUsers();

        spyClass.logInAs(LoginRole.CONSUMER);

        verify(spyClass.authorizationRule).setRole(UserIdentifier.getProducer());
    }

    @Test
    public void testDifferentRoles() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.parse("role:ROLE2"));
        spyClass.initializeUsers();

        verify(spyClass).createUser(anyString(), eq("Test"), eq("Producer"), eq(Role.ROLE1), eq(LoginRole.PRODUCER));
        verify(spyClass).createUser(anyString(), eq("Test"), eq("Consumer"), eq(Role.ROLE2), eq(LoginRole.CONSUMER));

        assertThat(spyClass.getConsumer(), notNullValue());
        assertThat(spyClass.getConsumer(), is(not((spyClass.getProducer()))));
    }

    @Test
    public void testDifferentUsers() {
        TestClass spyClass = mockTestClass();

        when(spyClass.getUserByUsername("user1")).thenReturn("test-user1");
        when(spyClass.getUserByUsername("user2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("user:user1"), UserIdentifier.parse("user:user2"));
        spyClass.initializeUsers();

        assertThat(spyClass.getProducer(), is("test-user1"));
        assertThat(spyClass.getConsumer(), is("test-user2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidProducerRole_Producer() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.getProducer(), UserIdentifier.parse("user:user2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPriducerRole_WithProducerRole() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.getWithProducerRole(), UserIdentifier.parse("user:user2"));
    }


    @Test
    public void testLoginAs_RoleUser() {
        TestClass spyClass = mockTestClass();
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        when(spyClass.getRandomUsername()).thenReturn("user1", "user2");

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.parse("role:ROLE2"));
        spyClass.initializeUsers();
        spyClass.logInAs(LoginRole.CONSUMER);

        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("user2");

        verify(spyClass.authorizationRule).setRole(new UserIdentifier(UserIdentifier.Type.ROLE, "ROLE2"));
    }

    @Test
    public void testLoginAs_ExistingUser() {
        TestClass spyClass = mockTestClass();
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        when(spyClass.getRandomUsername()).thenReturn("user1", "user2");
        when(spyClass.getUserByUsername("user2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.parse("user:user2"));
        spyClass.initializeUsers();
        spyClass.logInAs(LoginRole.CONSUMER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("test-user2");

        verify(spyClass.authorizationRule).setRole(new UserIdentifier(UserIdentifier.Type.USER, "user2"));
        verify(spyClass, times(1)).getUserByUsername("user2");
    }

    @Test
    public void testLoginAs_ProducerUser() {
        TestClass spyClass = mockTestClass();
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        when(spyClass.getRandomUsername()).thenReturn("user1", "user2");
        when(spyClass.getUserByUsername("user2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getProducer());
        spyClass.initializeUsers();
        // Login so that LoginRole.CONSUMER uses the current producer
        spyClass.logInAs(LoginRole.CONSUMER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("user1");

        verify(spyClass.authorizationRule).setRole(UserIdentifier.getProducer());
    }

    @Test(expected = IllegalStateException.class)
    public void testNewUserNotAllowedWithExistingProducer() {
        TestClass spyClass = mockTestClass();
        spyClass.setUsers(UserIdentifier.parse("user:username"), UserIdentifier.getWithProducerRole());
        spyClass.initializeUsers();
    }

    @Test
    public void testUserAsProducerWhenProducerExistingUser() {
        TestClass spyClass = mockTestClass();
        when(spyClass.getUserByUsername("username")).thenReturn("test-user");

        spyClass.setUsers(UserIdentifier.parse("user:username"), UserIdentifier.getProducer());
        spyClass.initializeUsers();

        // Login so that LoginRole.CONSUMER uses the current producer
        spyClass.logInAs(LoginRole.CONSUMER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("test-user");
    }

    @Test
    public void testUserWhenProducerExistingUser() {
        TestClass spyClass = mockTestClass();
        when(spyClass.getUserByUsername("username")).thenReturn("test-user");
        when(spyClass.getUserByUsername("username2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("user:username"), UserIdentifier.parse("user:username2"));
        spyClass.initializeUsers();

        // Login so that LoginRole.CONSUMER uses the current producer
        spyClass.logInAs(LoginRole.CONSUMER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("test-user2");
    }

    @Test
    public void testUserWhenAnonymous() {
        TestClass spyClass = mockTestClass();
        when(spyClass.getUserByUsername("username")).thenReturn("test-user");

        spyClass.setUsers(UserIdentifier.parse("user:username"), UserIdentifier.getAnonymous());
        spyClass.initializeUsers();

        spyClass.logInAs(LoginRole.CONSUMER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginAnonymous();
        order.verify(spyClass).loginWithUser(null);
    }

    @Test
    public void testProducerWhenAnonymous() {
        TestClass spyClass = mockTestClass();
        when(spyClass.getUserByUsername("username")).thenReturn("test-user");

        spyClass.setUsers(UserIdentifier.getAnonymous(), UserIdentifier.parse("user:username"));
        spyClass.initializeUsers();

        spyClass.logInAs(LoginRole.PRODUCER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginAnonymous();
        order.verify(spyClass).loginWithUser(null);
    }

    protected void mockDefaultCalls(TestClass spyClass) {
        when(spyClass.createUser(anyString(), anyString(), anyString(), any(Role.class), any(LoginRole.class)))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return (String) invocation.getArguments()[0];
                    }
                });
    }

    private TestClass mockTestClass() {
        TestClass testClass = new TestClass();
        TestClass spyClass = spy(testClass);
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        mockDefaultCalls(spyClass);
        return spyClass;
    }

}
