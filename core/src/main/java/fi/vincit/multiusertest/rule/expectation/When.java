package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.Collection;
import java.util.function.Supplier;

public interface When<EXPECTATION extends TestExpectation> {

    // Consumer

    /**
     * Sets the active consumers for assertions. Called by using methods found in {@link UserIdentifiers}.
     * @param userIdentifiers User identifier collections
     * @return Expectation API object
     * @since 1.0
     */
    ThenProducer<EXPECTATION> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers);

    /**
     * Sets the active consumers for assertions. Called with zero or more {@link UserIdentifier} objects.
     * In most cases {@link #whenCalledWithAnyOf(UserIdentifierCollection...)} is preferred since it works well
     * with {@link UserIdentifiers} helper methods.
     * @param userIdentifiers User identifiers
     * @return Expectation API object
     * @since 1.0
     */
    ThenProducer<EXPECTATION> whenCalledWithAnyOf(UserIdentifier... userIdentifiers);

    /**
     * Sets the active consumers for assertions. Called with a list of {@link UserIdentifier} objects.
     * @param userIdentifiers Collection of user identifiers
     * @return Expectation API object
     * @since 1.0
     */
    ThenProducer<EXPECTATION> whenCalledWithAnyOf(Collection<UserIdentifier> userIdentifiers);

    /**
     * Sets the active consumers for assertions. Called with a supplier method that returns a collection of identifiers.
     * Calling this will clear active producer identifiers.
     * @param userIdentifierSupplier Supplier method
     * @return Expectation API object
     * @since 1.0
     */
    ThenProducer<EXPECTATION> whenCalledWithAnyOf(Supplier<Collection<UserIdentifier>> userIdentifierSupplier);

}
