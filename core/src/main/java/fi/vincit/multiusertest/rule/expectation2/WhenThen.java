package fi.vincit.multiusertest.rule.expectation2;

public interface WhenThen<T extends TestExpectation> extends When<T>, Then<T> {

    void test() throws Throwable;

}
