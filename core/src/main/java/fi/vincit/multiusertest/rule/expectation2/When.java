package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface When<EXPECTATION extends TestExpectation> {

    /**
     * Set user identifiers to be used with the {@link Then#then(TestExpectation)}
     * call. {@link Then#then(TestExpectation)} should be called immediately
     * after this call to add the expectation.
     * @param userIdentifiers User identifiers
     * @return
     */
    WhenThen<EXPECTATION> whenCalledWith(UserIdentifiers... userIdentifiers);

    /**
     * Shorthand method for <pre>whenCalledWith(anyOf(UserIdentifierCollection...))</pre>
     * @since 0.5
     * @param userIdentifiers User identifier collections
     * @return
     */
    WhenThen<EXPECTATION> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers);

    /**
     * Shorthand method for <pre>whenCalledWith(anyOf(String...))</pre>
     * @since 0.5
     * @param userIdentifiers User identifier collections
     * @return
     */
    WhenThen<EXPECTATION> whenCalledWithAnyOf(String... userIdentifiers);


    /**
     * Set user identifiers to be used with the {@link Then#then(TestExpectation)}
     * call. {@link Then#then(TestExpectation)} should be called immediately
     * after this call to add the expectation.
     * @param userIdentifiers
     * @return
     */
    WhenThen<EXPECTATION> whenCalledWith(UserIdentifier... userIdentifiers);
}
