package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.util.UserIdentifier;

public interface Then<T extends TestExpectation> {

    /**
     * Make sure to call {@link When#whenCalledWithAnyOf(fi.vincit.multiusertest.util.UserIdentifierCollection...)} or {@link When#whenCalledWithAnyOf(UserIdentifier...)}
     * first to set the user identifiers this call will add the expectation.
     * @param testExpectation Expectation to test for the previous when identifiers
     * @return Expectation API object
     * @since 1.0
     */
    WhenThen<T> then(T testExpectation);

    /**
     * Define default expectation.
     * @param testExpectation Default expectation
     * @return Expectation API object
     * @since 1.0
     */
    WhenThen<T> otherwise(T testExpectation);

    /**
     * Define default expectation.
     * Alias for {@link #otherwise(TestExpectation)}.
     * @param testExpectation Default expectation
     * @return Expectation API object
     * @since 1.0
     */
    WhenThen<T> byDefault(T testExpectation);
}
