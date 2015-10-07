package fi.vincit.multiusertest.rule;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * <p>
 * Rule to be used with {@link MultiUserTestRunner} to define whether a test passes or
 * fails. Before a method to be tested is executed {@link #expect(Authentication)}
 * should be called with the identifiers or expectation.
 * </p>
 */
public class AuthorizationRule implements TestRule {

    private final Set<UserIdentifier> expectToFailOnRoles = new HashSet<>();
    private UserIdentifier userIdentifier;
    private FailMode failMode = FailMode.NONE;
    private Class<? extends Throwable> expectedException;
    private static final Statement NO_BASE = null;

    /**
     * Simple assertion for checking that method throws/doesn't throw an exception. If after the call no
     * exceptions are expected the {@link AuthorizationRule#dontExpectToFail()} has to be called. To expect
     * a custom exception, {@link #setExpectedException(Class)} can be used.
     * @param identifiers Identifiers for which the calls after this method are expected to fail.
     * @return AuthorizationRule for chaining calls
     * @since 0.1
     */
    public AuthorizationRule expect(Authentication identifiers) {
        addIdentifiers(identifiers);
        return this;
    }

    /**
     * Advanced assertion which makes use of expectations to form {@link fi.vincit.multiusertest.rule.expection.Expectations}.
     * Immediately executes the call to the given method and applies defined assertions. Using this
     * method, the {@link #dontExpectToFail()} method isn't required to be called to call other methods that are
     * not expected to fail.
     * @param expectation {@link fi.vincit.multiusertest.rule.expection.Expectation} rule
     * @throws Throwable If error occurs
     * @since 0.2
     */
    public void expect(Expectation expectation) throws Throwable {
        Objects.requireNonNull(expectedException, "Expected exception must be configured");
        expectation.setExpectedException(expectedException);
        expectation.execute(userIdentifier);
    }

    private void addIdentifiers(Authentication identifiers) {
        dontExpectToFail();
        for (UserIdentifier userIdentifier : identifiers.getIdentifiers()) {
            expectToFailOnRoles.add(userIdentifier);
        }
        this.failMode = identifiers.getFailMode();
    }

    public void setRole(UserIdentifier identifier) {
        this.userIdentifier = new UserIdentifier(identifier.getType(), identifier.getIdentifier());
    }

    /**
     * Set a custom exception to expect. Works only with {@link AuthorizationRule#expect(Authentication)}
     * method (version 0.1 style simple assertion).
     * @param expectedException Exception class to except
     */
    public void setExpectedException(Class<? extends Throwable> expectedException) {
        Objects.requireNonNull(expectedException, "Cannot set expected exception class to null");
        this.expectedException = expectedException;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new AuthChecker(base);
    }

    /**
     * Checks that if any previous calls should've failed and throws
     * {@link AssertionError} if the calls haven't behaved as expected.
     * If unexpected exception is thrown, throws {@link RuntimeException}.
     * After this call don't expect any call to fail.
     */
    public void dontExpectToFail() {
        try {
            new AuthChecker(NO_BASE).evaluate();
            expectToFailOnRoles.clear();
            failMode = FailMode.NONE;
        } catch(AssertionError ae) {
            throw new AssertionError("Expected to fail before call", ae);
        } catch (Throwable e) {
            throw new RuntimeException("Internal error", e);
        }
    }

    Set<UserIdentifier> getExpectToFailOnRoles() {
        return expectToFailOnRoles;
    }

    UserIdentifier getUserIdentifier() {
        return userIdentifier;
    }

    FailMode getFailMode() {
        return failMode;
    }

    Class<? extends Throwable> getExpectedException() {
        return expectedException;
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
            } catch (Throwable e) {
                if (expectedException == null) {
                    throw new RuntimeException("Expected exception not set yet", e);
                }
                if (expectedException.isInstance(e)) {
                    if (!evaluateExpectToFailCondition()) {
                        throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
                    } else {
                        return;
                    }
                } else {
                    throw e;
                }
            }

            if (evaluateExpectToFailCondition()) {
                throw new AssertionError("Expected to fail with user role " + userIdentifier.toString());
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
