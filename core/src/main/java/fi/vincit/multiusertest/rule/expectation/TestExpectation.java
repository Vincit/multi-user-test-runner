package fi.vincit.multiusertest.rule.expectation;

public interface TestExpectation {

    /**
     * Called when exception is not thrown after calling the call under test. Should
     * throw an {@link AssertionError} exception is expected in the implemented {@link TestExpectation}.
     * @param consumerProducerSet User identifiers used when calling the call under test
     */
    void handleExceptionNotThrown(ConsumerProducerSet consumerProducerSet);

    /**
     * Called when exception is thrown after calling the call under test. Should
     * throw an {@link AssertionError} exception if the exception is not expected or the exception is
     * not otherwise expected (e.g. custom assertion fails).
     * @param consumerProducerSet User identifiers used when calling the call under test
     * @param thrownException Thrown exception
     * @throws Throwable
     */
    void handleThrownException(ConsumerProducerSet consumerProducerSet, Throwable thrownException) throws Throwable;

}
