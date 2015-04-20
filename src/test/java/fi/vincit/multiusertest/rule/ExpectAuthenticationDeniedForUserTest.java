package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class ExpectAuthenticationDeniedForUserTest {

    @Parameterized.Parameters(name = "User: {0}, expected: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {UserIdentifier.getCreator(), TestUsers.CREATOR},
                {UserIdentifier.getNewUser(), TestUsers.NEW_USER},
                {UserIdentifier.parse("role:foo"), "role:foo"},
                {UserIdentifier.parse("user:bar"), "user:bar"}
        });
    }

    private UserIdentifier usedIdentifier;
    private String expectedIdentifier;

    public ExpectAuthenticationDeniedForUserTest(UserIdentifier usedIdentifier, String expectedIdentifier) {
        this.usedIdentifier = usedIdentifier;
        this.expectedIdentifier = expectedIdentifier;
    }

    @Test(expected = AssertionError.class)
    public void expectFail_NotThrownWhenExpected() throws Throwable {
        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockAndApply(rule);

        rule.setRole(usedIdentifier);
        rule.expectToFailIfUserAnyOf(expectedIdentifier);

        statement.evaluate();
    }

    @Test(expected = AssertionError.class)
    public void expectFail_ThrownWhenNotExpected() throws Throwable {
        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockApplyAndThrow(rule);

        rule.setRole(usedIdentifier);

        statement.evaluate();
    }

    @Test
    public void expectNotFail_TestPasses() throws Throwable {
        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockAndApply(rule);

        rule.setRole(usedIdentifier);

        statement.evaluate();
    }

    @Test
    public void expectNotFail_WhenAccessDeniedThrown() throws Throwable {
        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockApplyAndThrow(rule);

        rule.setRole(usedIdentifier);
        rule.expectToFailIfUserAnyOf(expectedIdentifier);

        statement.evaluate();
    }

    @Test(expected = AssertionError.class)
    public void clearExpectation() throws Throwable {
        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockApplyAndThrow(rule);

        rule.setRole(usedIdentifier);
        rule.expectToFailIfUserAnyOf(expectedIdentifier);
        rule.dontExpectToFail();

        statement.evaluate();
    }

    protected Statement mockAndApply(ExpectAuthenticationDeniedForUser rule) {
        Statement mockStatement = mock(Statement.class);
        Description mockDescription = mock(Description.class);
        return rule.apply(mockStatement, mockDescription);
    }

    protected Statement mockApplyAndThrow(ExpectAuthenticationDeniedForUser rule) throws Throwable {
        Statement mockStatement = mock(Statement.class);
        doThrow(new AccessDeniedException("Test expection")).when(mockStatement).evaluate();
        Description mockDescription = mock(Description.class);
        return rule.apply(mockStatement, mockDescription);
    }
}
