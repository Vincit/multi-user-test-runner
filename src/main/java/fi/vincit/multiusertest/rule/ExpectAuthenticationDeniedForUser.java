package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashSet;
import java.util.Set;

/**
 * Rule to be used with {@link fi.vincit.multiusertest.runner.MultiUserTestRunner} to define whether a test passes or
 * fails. Before a method to be tested is executed {@link ExpectAuthenticationDeniedForUser#expect(Authentication)}
 * should be called with the users that are expected to fail. The user syntax is same as in
 * {@link fi.vincit.multiusertest.runner.MultiUserTestRunner}
 *
 */
public class ExpectAuthenticationDeniedForUser implements TestRule {

    private Set<UserIdentifier> expectToFailOnRoles = new HashSet<UserIdentifier>();
    private UserIdentifier userIdentifier;
    private FailMode failMode = FailMode.NONE;
    private static final Statement NO_BASE = null;

    public ExpectAuthenticationDeniedForUser expect(Authentication identifiers) {
        addIdentifiers(identifiers);
        return this;
    }

    private void addIdentifiers(Authentication identifiers) {
        dontExpectToFail();
        for (String identifier : identifiers.getIdentifiers()) {
            expectToFailOnRoles.add(UserIdentifier.parse(identifier));
        }
        this.failMode = identifiers.getFailMode();
    }

    public void setRole(UserIdentifier identifier) {
        setRole(identifier.getType(), identifier.getIdentifier());
    }

    public void setRole(UserIdentifier.Type type, String identifier) {
        this.userIdentifier = new UserIdentifier(type, identifier);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new AuthChecker(base);
    }

    public void dontExpectToFail() {
        try {
            new AuthChecker(NO_BASE).evaluate();
            expectToFailOnRoles.clear();
            failMode = FailMode.NONE;
        } catch(AssertionError ae) {
            throw new AssertionError("Expected to fail before dontExpectToFailCall", ae);
        } catch (Throwable e) {
            throw new RuntimeException("Internal error", e);
        }
    }

    private class AuthChecker extends Statement {

        private Statement next;

        public AuthChecker(Statement next) {
            this.next = next;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                if (next != null) {
                    next.evaluate();
                }

                boolean expectToFail = evaluateExpectToFailCondition();
                if (expectToFail) {
                    throw new AssertionError("Expected to fail with user role " + userIdentifier.toString());
                }
            } catch (AccessDeniedException e) {
                boolean expectToFail = evaluateExpectToFailCondition();
                if (!expectToFail) {
                    throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
                }
            }
        }

        private boolean evaluateExpectToFailCondition() {
            if (failMode == FailMode.EXPECT_FAIL) {
                return expectToFailOnRoles.contains(userIdentifier);
            } else if (failMode == FailMode.EXPECT_NOT_FAIL) {
                return !expectToFailOnRoles.contains(userIdentifier);
            } else {
                return false;
            }
        }
    }
}
