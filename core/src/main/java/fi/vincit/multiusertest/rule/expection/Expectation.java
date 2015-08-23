package fi.vincit.multiusertest.rule.expection;

import fi.vincit.multiusertest.util.UserIdentifier;

public interface Expectation {

    /**
     * Executes the call and processes the outcome
     * @param userIdentifier UserIdentifier to use for the call context.
     * @throws AssertionError if the outcome is not expected.
     */
    void execute(UserIdentifier userIdentifier) throws Throwable;

    void setExpectedException(Class<? extends Throwable> expectedException);
}
