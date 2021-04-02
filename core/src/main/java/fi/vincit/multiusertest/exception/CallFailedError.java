package fi.vincit.multiusertest.exception;

import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;

public class CallFailedError extends AssertionError {

    public static AssertionError expectCallNotToFail(ConsumerProducerSet consumerProducerSet, Throwable exception) {
        return new CallFailedError(String.format(
                "Assertion failed with role <%s>: %s",
                consumerProducerSet,
                exception
        ), exception);
    }

    public static AssertionError expectedCallToFail(ConsumerProducerSet consumerProducerSet, Class<? extends Throwable> expected) {
        return new CallFailedError(String.format(
                "Expected assertion to fail with role <%s> with exception %s. " +
                "No exception was thrown.",
                consumerProducerSet.toString(),
                expected.getName()
        ));
    }

    public static AssertionError unexpectedException(ConsumerProducerSet consumerProducerSet, Class<? extends Throwable> expected, Throwable thrown) {
        return new CallFailedError(String.format(
                "Unexpected exception thrown with role <%s>: " +
                "Expected <%s> but was <%s>: " +
                "%s",
                consumerProducerSet,
                expected.getSimpleName(),
                thrown.getClass().getSimpleName(),
                thrown.getMessage()
        ), thrown);
    }

    private CallFailedError(String s, Throwable throwable) {
        super(s, throwable);
    }

    private CallFailedError(Object o) {
        super(o);
    }
}
