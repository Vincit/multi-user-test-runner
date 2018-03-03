package fi.vincit.multiusertest.rule.expectation;

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
     * @since 1.0
     */
    Then<EXPECTATION> whenCalledWith(UserIdentifiers... userIdentifiers);

    /**
     * Shorthand method for <pre>whenCalledWith(anyOf(UserIdentifierCollection...))</pre>
     * @param userIdentifiers User identifier collections
     * @return
     * @since 1.0
     */
    Then<EXPECTATION> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers);

    /**
     * Shorthand method for <pre>whenCalledWith(anyOf(String...))</pre>
     * @param userIdentifiers User identifier collections
     * @return
     * @since 1.0
     */
    Then<EXPECTATION> whenCalledWithAnyOf(String... userIdentifiers);


    /**
     * Set user identifiers to be used with the {@link Then#then(TestExpectation)}
     * call. {@link Then#then(TestExpectation)} should be called immediately
     * after this call to add the expectation.
     * @param userIdentifiers
     * @return
     * @since 1.0
     */
    Then<EXPECTATION> whenCalledWith(UserIdentifier... userIdentifiers);
}
