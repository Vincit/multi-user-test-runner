package fi.vincit.multiusertest.rule.expectation2;

public interface WhenThen<T extends TestExpectation> extends When<T>, Then<T> {

    /**
     * Execute the call under test and run assertions
     * @throws Throwable
     */
    void test() throws Throwable;

}
