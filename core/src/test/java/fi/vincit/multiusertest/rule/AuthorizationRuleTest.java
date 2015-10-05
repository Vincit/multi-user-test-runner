package fi.vincit.multiusertest.rule;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.util.UserIdentifier;

public class AuthorizationRuleTest {

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

}
