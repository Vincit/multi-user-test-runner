package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private enum Mode {
        FAIL_IF_ANY_OF,
        NOT_FAIL_IF_ANY_OF,
        NONE;

        @Override
        public String toString() {
            switch (this) {
                case FAIL_IF_ANY_OF: return "any of";
                case NOT_FAIL_IF_ANY_OF: return "not any of";
                case NONE: return "none";
            }
            return super.toString();
        }
    }

    private enum ExceptionMode {
        EXPECT,
        DONT_EXPECT;

        @Override
        public String toString() {
            switch (this) {
                case EXPECT: return "expect exception";
                case DONT_EXPECT: return "do not expect exception";
            }
            return super.toString();
        }
    }

    @Parameterized.Parameters(name = "Given used by {0}. When {1} <{2}>. Then {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {UserIdentifier.getCreator(), Mode.FAIL_IF_ANY_OF, Identifiers.of(TestUsers.CREATOR), ExceptionMode.EXPECT},
                {UserIdentifier.getCreator(), Mode.FAIL_IF_ANY_OF, Identifiers.of("role:foo", TestUsers.CREATOR), ExceptionMode.EXPECT},
                {UserIdentifier.getNewUser(), Mode.FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER), ExceptionMode.EXPECT},
                {UserIdentifier.parse("role:foo"), Mode.FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "role:foo", "user:bar"), ExceptionMode.EXPECT},
                {UserIdentifier.parse("user:bar"), Mode.FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "role:foo", "user:bar"), ExceptionMode.EXPECT},

                {UserIdentifier.getCreator(), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of(TestUsers.CREATOR), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.getCreator(), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of("role:foo", TestUsers.CREATOR), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.getNewUser(), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.parse("role:foo"), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "role:foo", "user:bar"), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.parse("user:bar"), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "role:foo", "user:bar"), ExceptionMode.DONT_EXPECT},


                {UserIdentifier.getCreator(), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of("user:user"), ExceptionMode.EXPECT},
                {UserIdentifier.getCreator(), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of("role:foo", "user:user"), ExceptionMode.EXPECT},
                {UserIdentifier.getNewUser(), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of("role:foo"), ExceptionMode.EXPECT},
                {UserIdentifier.parse("role:foo"), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "user:bar"), ExceptionMode.EXPECT},
                {UserIdentifier.parse("user:bar"), Mode.NOT_FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "role:foo"), ExceptionMode.EXPECT},

                {UserIdentifier.getCreator(), Mode.FAIL_IF_ANY_OF, Identifiers.of("user:user"), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.getCreator(), Mode.FAIL_IF_ANY_OF, Identifiers.of("role:foo", "user:user"), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.getNewUser(), Mode.FAIL_IF_ANY_OF, Identifiers.of("role:foo"), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.parse("role:foo"), Mode.FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "user:bar"), ExceptionMode.DONT_EXPECT},
                {UserIdentifier.parse("user:bar"), Mode.FAIL_IF_ANY_OF, Identifiers.of(TestUsers.NEW_USER, "role:foo"), ExceptionMode.DONT_EXPECT}
        });
    }

    private UserIdentifier usedIdentifier;
    private Identifiers expectedIdentifier;
    private Mode failMode;
    private ExceptionMode exceptionMode;

    public ExpectAuthenticationDeniedForUserTest(UserIdentifier usedIdentifier,
                                                 Mode failMode,
                                                 Identifiers expectedIdentifier,
                                                 ExceptionMode exceptionMode) {
        this.usedIdentifier = usedIdentifier;
        this.expectedIdentifier = expectedIdentifier;
        this.failMode = failMode;
        this.exceptionMode = exceptionMode;
    }

    @Test
    public void evaluateTest() throws Throwable {
        if (exceptionMode == ExceptionMode.EXPECT) {
            expectedException.expect(AssertionError.class);
        }

        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockAndApply(rule);

        rule.setRole(usedIdentifier);
        if (failMode == Mode.FAIL_IF_ANY_OF) {
            rule.expectToFailIfUserAnyOf(expectedIdentifier.getIdentifiers());
        } else if (failMode == Mode.NOT_FAIL_IF_ANY_OF) {
            rule.expectNotToFailIfUserAnyOf(expectedIdentifier.getIdentifiers());
        }

        statement.evaluate();
    }


    @Test(expected = AssertionError.class)
    public void clearExpectation() throws Throwable {
        ExpectAuthenticationDeniedForUser rule = new ExpectAuthenticationDeniedForUser();

        Statement statement = mockApplyAndThrow(rule);

        rule.setRole(usedIdentifier);
        if (failMode == Mode.FAIL_IF_ANY_OF) {
            rule.expectToFailIfUserAnyOf(expectedIdentifier.getIdentifiers());
        } else if (failMode == Mode.NOT_FAIL_IF_ANY_OF) {
            rule.expectNotToFailIfUserAnyOf(expectedIdentifier.getIdentifiers());
        }
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
