package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.Collection;
import java.util.function.Supplier;

public interface WhenProducer<EXPECTATION extends TestExpectation> {

    /**
     * Sets the active producer to be any.
     * Calling this will clear active consumer identifiers.
     * @return Expectation API object
     * @since 1.0
     */
    When<EXPECTATION> whenProducerIsAny();

    /**
     * Sets the active producers for assertions. Called by using methods found in {@link UserIdentifiers}.
     * Calling this will clear active consumer identifiers.
     * @param producerIdentifiers Producer identifier collections
     * @return Expectation API object
     * @since 1.0
     */
    When<EXPECTATION> whenProducerIsAnyOf(UserIdentifierCollection... producerIdentifiers);

    /**
     * Sets the active producers for assertions. Called with zero or more {@link UserIdentifier} objects.
     * In most cases {@link #whenProducerIsAnyOf(UserIdentifierCollection...)} is preferred since it works
     * well with {@link UserIdentifiers} helper methods.
     * Calling this will clear active consumer identifiers.
     * @param producerIdentifiers User identifiers
     * @return Expectation API object
     * @since 1.0
     */
    When<EXPECTATION> whenProducerIsAnyOf(UserIdentifier... producerIdentifiers);

    /**
     * Sets the active producers for assertions. Called with a list of {@link UserIdentifier} objects.
     * Calling this will clear active consumer identifiers.
     * @param producerIdentifiers Collection of user identifiers
     * @return Expectation API object
     * @since 1.0
     */
    When<EXPECTATION> whenProducerIsAnyOf(Collection<UserIdentifier> producerIdentifiers);

    /**
     * Sets the active producers for assertions. Called with a supplier method that returns a collection of identifiers
     * @param producerIdentifierSupplier Supplier method
     * @return Expectation API object
     * @since 1.0
     */
    When<EXPECTATION> whenProducerIsAnyOf(Supplier<Collection<UserIdentifier>> producerIdentifierSupplier);
}
