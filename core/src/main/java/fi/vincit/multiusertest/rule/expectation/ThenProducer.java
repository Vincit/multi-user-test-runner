package fi.vincit.multiusertest.rule.expectation;

public interface ThenProducer<T extends TestExpectation> extends Then<T>, WhenProducer<T> {
}
