package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class ExpectAuthenticationDeniedForUserTest_SetException {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void dontCatchWrongExceptions() throws Throwable {
        expectedException.expect(AccessDeniedException.class);

        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new AccessDeniedException("")).evaluate();
    }

    @Test
    public void catchSetExceptions() throws Throwable {
        expectedException.expect(AssertionError.class);

        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new IllegalArgumentException("")).evaluate();
    }

    @Test
    public void catchSetSuperClassExceptions() throws Throwable {
        expectedException.expect(AssertionError.class);

        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new NumberFormatException("")).evaluate();
    }

    protected Statement mockApplyAndThrow(ExpectAuthenticationDeniedForUser rule, Exception exception) throws Throwable {
        Statement mockStatement = mock(Statement.class);
        doThrow(exception).when(mockStatement).evaluate();
        Description mockDescription = mock(Description.class);
        return rule.apply(mockStatement, mockDescription);
    }


}
