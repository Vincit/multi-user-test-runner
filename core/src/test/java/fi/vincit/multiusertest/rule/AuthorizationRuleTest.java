package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AuthorizationRuleTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void expect() throws Throwable {
        AuthorizationRule rule = new AuthorizationRule();
        rule.setExpectedException(IllegalStateException.class);
        rule.setRole(UserIdentifier.parse("user:Foo"));
        Expectation expectation = mock(Expectation.class);

        rule.expect(expectation);

        verify(expectation).execute(UserIdentifier.parse("user:Foo"));
    }

    @Test(expected = AssertionError.class)
    public void expectThrowsError() throws Throwable {
        AuthorizationRule rule = new AuthorizationRule();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.parse("user:Foo"));
        Expectation expectation = mock(Expectation.class);
        doThrow(AssertionError.class).when(expectation).execute(any(UserIdentifier.class));

        rule.expect(expectation);

        verify(expectation).setExpectedException(IllegalArgumentException.class);
        verify(expectation).execute(UserIdentifier.parse("user:Foo"));
    }

    @Test
    public void setExpectedException() {
        AuthorizationRule rule = new AuthorizationRule();

        rule.setExpectedException(RuntimeException.class);

        assertThat(rule.getExpectedException().getName(), is(RuntimeException.class.getName()));
    }

    @Test(expected = NullPointerException.class)
    public void setExpectedException_SetNull() {
        AuthorizationRule rule = new AuthorizationRule();

        rule.setExpectedException(null);
    }

    @Test
    public void evaluate_NoExpectedExceptionSet() throws Throwable {
        AuthorizationRule rule = new AuthorizationRule();

        Statement statement = mock(Statement.class);
        Description description = mock(Description.class);

        Exception cause = new RuntimeException();
        doThrow(cause).when(statement).evaluate();

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(is("Expected exception not set yet"));
        expectedException.expectCause(is(cause));

        rule.apply(statement, description).evaluate();
    }

    @Test
    public void evaluate_ExpectedExceptionThrown() throws Throwable {
        String role = "role:ROLE_ADMIN";
        Exception cause = new IOException();
        Statement statement = mock(Statement.class);
        Description description = mock(Description.class);

        doThrow(cause).when(statement).evaluate();


        AuthorizationRule rule = new AuthorizationRule();
        rule.setRole(UserIdentifier.parse(role));
        rule.setExpectedException(cause.getClass());
        rule.expect(Authentication.toFail(UserIdentifiers.ifAnyOf(role)));

        rule.apply(statement, description).evaluate();
    }

    @Test
    public void evaluate_UnexpectedExceptionThrown() throws Throwable {
        String role = "role:ROLE_ADMIN";
        Exception cause = new IOException();
        Exception unexpectedException = new RuntimeException();
        Statement statement = mock(Statement.class);
        Description description = mock(Description.class);

        doThrow(unexpectedException).when(statement).evaluate();


        AuthorizationRule rule = new AuthorizationRule();
        rule.setRole(UserIdentifier.parse(role));
        rule.setExpectedException(cause.getClass());
        rule.expect(Authentication.toFail(UserIdentifiers.ifAnyOf(role)));


        expectedException.expect(RuntimeException.class);

        rule.apply(statement, description).evaluate();
    }

    @Test
    public void dontExpectToFail_ShouldveFailed() throws Throwable {
        String role = "role:ROLE_ADMIN";
        Exception cause = new IOException();

        AuthorizationRule rule = new AuthorizationRule();
        rule.setRole(UserIdentifier.parse(role));
        rule.setExpectedException(cause.getClass());
        rule.expect(Authentication.toFail(UserIdentifiers.ifAnyOf(role)));
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected to fail before call");

        rule.dontExpectToFail();
    }

    @Test
    public void dontExpectToFail_NoFail() throws Throwable {
        AuthorizationRule rule = new AuthorizationRule();
        rule.dontExpectToFail();
    }
}
