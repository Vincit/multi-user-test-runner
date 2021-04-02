package fi.vincit.multiusertest.rule.expectation;

import java.util.function.Consumer;

public interface WhenThen<T extends TestExpectation> extends When<T>, ThenProducer<T> {

    /**
     * Execute the call under test and run assertions
     * @throws Throwable If an exception is thrown during the test
     * @since 1.0
     */
    void test() throws Throwable;

    /**
     * Prints role mappings using the given consumer to print them
     * @param logger Logger printer
     * @return Expectation API object
     */
    WhenThen<T> debugRoleMappings(Consumer<String> logger);
}
