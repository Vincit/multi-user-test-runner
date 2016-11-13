package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.util.UserIdentifier;

public interface TestExpectation {

    /**
     * Called when exception is not thrown after calling the call under test. Should
     * throw an {@link AssertionError} exception is expected in the implemented {@link TestExpectation}.
     * @param userIdentifier User identifier used when calling the call under test
     */
    void handleExceptionNotThrown(UserIdentifier userIdentifier);

    /**
     * Called when exception is thrown after calling the call under test. Should
     * throw an {@link AssertionError} exception if the exception is not expected or the exception is
     * not otherwise expected (e.g. custom assertion fails).
     * @param userIdentifier User identifier used when calling the call under test
     * @param thrownException Thrown exception
     * @throws Throwable
     */
    void handleThrownException(UserIdentifier userIdentifier, Throwable thrownException) throws Throwable;

}
