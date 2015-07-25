package fi.vincit.multiusertest.rule;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ExpectAuthenticationDeniedForUserTest_setModes {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void dontCatchWrongExceptions() throws Throwable {
        expectedException.expect(AccessDeniedException.class);

        AuthorizationRule rule = new AuthorizationRule();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new AccessDeniedException("")).evaluate();
    }

    @Test
    public void catchSetExceptions() throws Throwable {
        expectedException.expect(AssertionError.class);

        AuthorizationRule rule = new AuthorizationRule();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new IllegalArgumentException("")).evaluate();
    }

    @Test
    public void catchSetSuperClassExceptions() throws Throwable {
        expectedException.expect(AssertionError.class);

        AuthorizationRule rule = new AuthorizationRule();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new NumberFormatException("")).evaluate();
    }

    @Test
    public void throwDefaultException() throws Throwable {
        expectedException.expect(AssertionError.class);

        AuthorizationRule rule = new AuthorizationRule();
        rule.setExpectedException(IllegalArgumentException.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockApplyAndThrow(rule, new AssertionError("E")).evaluate();
    }

    @Test
    public void throwDefaultException_expectToFail() throws Throwable {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected to fail with user role");

        AuthorizationRule rule = new AuthorizationRule();
        rule.expect(toFail(ifAnyOf(TestUsers.NEW_USER)));
        rule.setExpectedException(AssertionError.class);
        rule.setRole(UserIdentifier.getNewUser());

        mockAndApply(rule).evaluate();
    }

    @Test
    public void failModeNoneByDefault() {
        AuthorizationRule rule = new AuthorizationRule();
        assertThat(rule.getFailMode(), is(FailMode.NONE));
    }

    @Test
    public void setExpectedFailCondition_ToFail() {
        AuthorizationRule rule = new AuthorizationRule();
        rule.expect(toFail(ifAnyOf("role:foo")));
        assertThat(rule.getFailMode(), is(FailMode.EXPECT_FAIL));
    }

    @Test
    public void setExpectedFailCondition_NotToFail() {
        AuthorizationRule rule = new AuthorizationRule();
        rule.expect(notToFail(ifAnyOf("role:foo")));
        assertThat(rule.getFailMode(), is(FailMode.EXPECT_NOT_FAIL));
    }

    private Statement mockAndApply(AuthorizationRule rule) {
        Statement mockStatement = mock(Statement.class);
        Description mockDescription = mock(Description.class);
        return rule.apply(mockStatement, mockDescription);
    }

    protected Statement mockApplyAndThrow(AuthorizationRule rule, Throwable exception) throws Throwable {
        Statement mockStatement = mock(Statement.class);
        doThrow(exception).when(mockStatement).evaluate();
        Description mockDescription = mock(Description.class);
        return rule.apply(mockStatement, mockDescription);
    }


}
