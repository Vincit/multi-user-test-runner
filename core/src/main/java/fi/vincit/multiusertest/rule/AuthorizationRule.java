package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.rule.expectation.FunctionCall;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.rule.expectation.WhenThen;
import fi.vincit.multiusertest.rule.expectation.call.FunctionCallWhenThen;
import fi.vincit.multiusertest.rule.expectation.value.ReturnValueWhenThen;
import fi.vincit.multiusertest.rule.expectation.value.TestValueExpectation;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * <p>
 * Rule to be used with {@link MultiUserTestRunner} to define whether a test passes or
 * fails.
 * </p>
 */
public class AuthorizationRule implements TestRule, Authorization {

    private UserIdentifier userIdentifier;
    private UserRoleIT userRoleIT;
    private boolean expectationConstructionFinished = false;

    @Override
    public void setUserRoleIT(UserRoleIT userRoleIT) {
        this.userRoleIT = userRoleIT;
    }

    @Override
    public void setRole(UserIdentifier identifier) {
        this.userIdentifier = new UserIdentifier(identifier.getType(), identifier.getIdentifier());
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new AuthChecker(base);
    }

    @Override
    public void markExpectationConstructed() {
        if (expectationConstructionFinished) {
            expectationConstructionFinished = false;
        } else {
            throw new IllegalStateException("Expectation configuration is not currently open");
        }
    }

    /**
     * Starts constructing expectation for a function call.
     * @param functionCall Call to test
     * @return Expectation object
     * @since 1.0
     */
    @Override
    public WhenThen<TestExpectation> given(FunctionCall functionCall) {
        expectationConstructionFinished = true;
        userRoleIT.logInAs(LoginRole.CONSUMER);
        return new FunctionCallWhenThen(functionCall, userIdentifier, this, userRoleIT);
    }

    /**
     * Starts constructing expectation for a return value call.
     * @param returnValueCall Call to test
     * @return Expectation API object
     * @since 1.0
     */
    @Override
    public <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> given(ReturnValueCall<VALUE_TYPE> returnValueCall) {
        expectationConstructionFinished = true;
        userRoleIT.logInAs(LoginRole.CONSUMER);
        return new ReturnValueWhenThen<>(
                returnValueCall,
                userIdentifier,
                this,
                userRoleIT
        );
    }

    private class AuthChecker extends Statement {

        private Statement next;

        public AuthChecker(Statement next) {
            this.next = next;
        }

        @Override
        public void evaluate() throws Throwable {
            if (next != null) {
                next.evaluate();
            }

            validateExpectationConstructionFinished();
        }

    }

    private void validateExpectationConstructionFinished() {
        if (expectationConstructionFinished) {
            throw new IllegalStateException("Expectation still in progress. " +
                    "Please call test() method. " +
                    "Otherwise the assertions are not run properly."
            );
        }
    }
}
