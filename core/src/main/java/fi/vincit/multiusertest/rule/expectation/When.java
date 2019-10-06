package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface When<EXPECTATION extends TestExpectation> {

    /**
     * Called by using methods found in {@link UserIdentifiers}
     * @param userIdentifiers User identifier collections
     * @return Expectation API object
     * @since 1.0
     */
    Then<EXPECTATION> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers);

    /**
     * Called with zero or more {@link UserIdentifier} objects. In most cases {@link #whenCalledWithAnyOf(UserIdentifierCollection...)}
     * is preferred since it works well with {@link UserIdentifiers} helper methods.
     * @param userIdentifiers User identifiers
     * @return Expectation API object
     * @since 1.0
     */
    Then<EXPECTATION> whenCalledWithAnyOf(UserIdentifier... userIdentifiers);

}