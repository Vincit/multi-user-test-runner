package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.rule.expectation2.TestExpectation;
import fi.vincit.multiusertest.rule.expectation2.WhenThen;
import fi.vincit.multiusertest.rule.expectation2.call.FunctionCallWhenThen;
import fi.vincit.multiusertest.rule.expectation2.value.ReturnValueWhenThen;
import fi.vincit.multiusertest.rule.expectation2.value.TestValueExpectation;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
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
public class AuthorizationRule implements TestRule {

    private UserIdentifier userIdentifier;
    private FailMode failMode = FailMode.NONE;
    private boolean expectation2ConstructionFinished = false;

    public void setRole(UserIdentifier identifier) {
        this.userIdentifier = new UserIdentifier(identifier.getType(), identifier.getIdentifier());
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new AuthChecker(base);
    }


    public FailMode getFailMode() {
        return failMode;
    }

    public void markExpectationConstructed() {
        if (expectation2ConstructionFinished) {
            expectation2ConstructionFinished = false;
        } else {
            throw new IllegalStateException("Expectation API 2 configuration is not currently open");
        }
    }

    /**
     * Starts constructing expectation for a function call with API 2 syntax.
     * @param functionCall Call to test
     * @return
     * @since 0.5
     */
    public WhenThen<TestExpectation> testCall(FunctionCall functionCall) {
        expectation2ConstructionFinished = true;
        return new FunctionCallWhenThen(functionCall, userIdentifier, this);
    }

    /**
     * Starts constructing expectation for a return value call with API 2 syntax.
     * @param returnValueCall Call to test
     * @return
     * @since 0.5
     */
    public <VALUE_TYPE> WhenThen<TestValueExpectation<VALUE_TYPE>> testCall(ReturnValueCall<VALUE_TYPE> returnValueCall) {
        expectation2ConstructionFinished = true;
        return new ReturnValueWhenThen<>(
                returnValueCall,
                userIdentifier,
                this
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
        if (expectation2ConstructionFinished) {
            throw new IllegalStateException("Expectation still in progress. " +
                    "Please call test() method. " +
                    "Otherwise the assertions are not run properly."
            );
        }
    }
}
