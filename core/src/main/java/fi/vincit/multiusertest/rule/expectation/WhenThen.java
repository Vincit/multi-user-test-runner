package fi.vincit.multiusertest.rule.expectation;

public interface WhenThen<T extends TestExpectation> extends When<T>, Then<T> {

    /**
     * Execute the call under test and run assertions
     * @throws Throwable If an exception is thrown during the test
     * @since 1.0
     */
    void test() throws Throwable;
}
