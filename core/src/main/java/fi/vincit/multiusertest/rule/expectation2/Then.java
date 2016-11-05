package fi.vincit.multiusertest.rule.expectation2;

public interface Then<T extends TestExpectation> {
    WhenThen<T> then(T testExpectation);
}
