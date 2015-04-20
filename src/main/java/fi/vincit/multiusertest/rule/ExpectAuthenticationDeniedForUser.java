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
 * fails. Before a method to be tested is executed {@link ExpectAuthenticationDeniedForUser#expectToFailIfUserAnyOf(String...)}
 * should be called with the users that are expected to fail. The user syntax is same as in
 * {@link fi.vincit.multiusertest.runner.MultiUserTestRunner}
 *
 *
 */
public class ExpectAuthenticationDeniedForUser implements TestRule {

    private Set<UserIdentifier> expectToFailOnRoles = new HashSet<UserIdentifier>();
    private UserIdentifier userIdentifier;

    public ExpectAuthenticationDeniedForUser expectToFailIfUserAnyOf(String... identifiers) {
        dontExpectToFail();
        for (String identifier : identifiers) {
            expectToFailOnRoles.add(UserIdentifier.parse(identifier));
        }
        return this;
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
        expectToFailOnRoles.clear();
    }

    private class AuthChecker extends Statement {

        private Statement next;

        public AuthChecker(Statement next) {
            this.next = next;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                next.evaluate();
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
            return expectToFailOnRoles.contains(userIdentifier);
        }
    }
}
