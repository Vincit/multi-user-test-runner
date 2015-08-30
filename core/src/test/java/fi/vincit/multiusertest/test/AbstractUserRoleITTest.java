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
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;

public class AbstractUserRoleITTest {

    public enum Role {
        ROLE1,
        ROLE2
    }

    @TestUsers(creators = "role:ROLE1", users = "role:ROLE1")
    @MultiUserTestConfig(runner = BlockMultiUserTestClassRunner.class)
    public static class TestClass extends AbstractUserRoleIT<String, Role> {
        @Override
        protected void loginWithUser(String user) {

        }

        @Override
        protected String createUser(String username, String firstName, String lastName, Role userRole, LoginRole loginRole) {
            return username;
        }

        @Override
        protected Role stringToRole(String role) {
            return Role.valueOf(role);
        }

        @Override
        protected String getUserByUsername(String username) {
            return null;
        }

        @Test
        public void mockTestMethod() {
        }
    }

    @Test
    public void testNewUser() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getNewUser());
        spyClass.initializeUsers();

        verify(spyClass).createUser(anyString(), eq("Test"), eq("Creator"), eq(Role.ROLE1), eq(LoginRole.CREATOR));
        verify(spyClass).createUser(anyString(), eq("Test"), eq("User"), eq(Role.ROLE1), eq(LoginRole.USER));

        assertThat(spyClass.getUser(), notNullValue());
        assertThat(spyClass.getUser(), is(not((spyClass.getCreator()))));
    }

    @Test
    public void testCreatorRole() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getCreator());
        spyClass.initializeUsers();

        verify(spyClass).createUser(anyString(), eq("Test"), eq("Creator"), eq(Role.ROLE1), eq(LoginRole.CREATOR));
        assertThat(spyClass.getUser(), notNullValue());
        assertThat(spyClass.getUser(), is(spyClass.getCreator()));
    }

    @Test
    public void testDifferentRoles() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.parse("role:ROLE2"));
        spyClass.initializeUsers();

        verify(spyClass).createUser(anyString(), eq("Test"), eq("Creator"), eq(Role.ROLE1), eq(LoginRole.CREATOR));
        verify(spyClass).createUser(anyString(), eq("Test"), eq("User"), eq(Role.ROLE2), eq(LoginRole.USER));

        assertThat(spyClass.getUser(), notNullValue());
        assertThat(spyClass.getUser(), is(not((spyClass.getCreator()))));
    }

    @Test
    public void testDifferentUsers() {
        TestClass spyClass = mockTestClass();

        when(spyClass.getUserByUsername("user1")).thenReturn("test-user1");
        when(spyClass.getUserByUsername("user2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("user:user1"), UserIdentifier.parse("user:user2"));
        spyClass.initializeUsers();

        assertThat(spyClass.getCreator(), is("test-user1"));
        assertThat(spyClass.getUser(), is("test-user2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCreatorRole_Creator() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.getCreator(), UserIdentifier.parse("user:user2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCreatorRole_NewUser() {
        TestClass spyClass = mockTestClass();

        spyClass.setUsers(UserIdentifier.getNewUser(), UserIdentifier.parse("user:user2"));
    }


    @Test
    public void testLoginAs_RoleUser() {
        TestClass spyClass = mockTestClass();
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        when(spyClass.getRandomUsername()).thenReturn("user1", "user2");

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.parse("role:ROLE2"));
        spyClass.initializeUsers();
        spyClass.logInAs(LoginRole.USER);

        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("user2");

        verify(spyClass.authorizationRule).setRole(UserIdentifier.Type.ROLE, "ROLE2");
    }

    @Test
    public void testLoginAs_ExistingUser() {
        TestClass spyClass = mockTestClass();
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        when(spyClass.getRandomUsername()).thenReturn("user1", "user2");
        when(spyClass.getUserByUsername("user2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.parse("user:user2"));
        spyClass.initializeUsers();
        spyClass.logInAs(LoginRole.USER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("test-user2");

        verify(spyClass.authorizationRule).setRole(UserIdentifier.Type.USER, "user2");
        verify(spyClass, times(1)).getUserByUsername("user2");
    }

    @Test
    public void testLoginAs_CreatorUser() {
        TestClass spyClass = mockTestClass();
        spyClass.authorizationRule = mock(AuthorizationRule.class);

        when(spyClass.getRandomUsername()).thenReturn("user1", "user2");
        when(spyClass.getUserByUsername("user2")).thenReturn("test-user2");

        spyClass.setUsers(UserIdentifier.parse("role:ROLE1"), UserIdentifier.getCreator());
        spyClass.initializeUsers();
        // Login so that LoginRole.USER uses the current creator
        spyClass.logInAs(LoginRole.USER);


        InOrder order = inOrder(spyClass);
        order.verify(spyClass).loginWithUser("user1");

        verify(spyClass.authorizationRule).setRole(UserIdentifier.getCreator());
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

        mockDefaultCalls(spyClass);
        return spyClass;
    }

}
