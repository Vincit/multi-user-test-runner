package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * Resolves user identifier instances for given login roles
 * according to the given user resolver rules.
 *
 * @param <USER> User type in the system under test
 * @param <ROLE> Role type in the system under test
 */
public class IdentifierResolver<USER, ROLE> {

    private RoleContainer<ROLE> consumer;
    private RoleContainer<ROLE> producer;

    public IdentifierResolver(UserResolver<USER, ROLE> userResolver) {
        this.consumer = userResolver.getConsumer();
        this.producer = userResolver.getProducer();
    }

    /**
     * Returns the user identifier that should be used with the
     * given login role. E.g. when resolving identifier for consumer
     * it may return producer identifier if the current consumer
     * identifier requires it.
     * @param loginRole Login role
     * @return User identifier
     */
    public UserIdentifier getIdentifierFor(LoginRole loginRole) {
        if (loginRole == LoginRole.PRODUCER) {
            return UserIdentifier.getProducer();
        } else {
            return getConsumerIdentifier();
        }
    }

    public UserIdentifier getProducerIdentifier() {
        return new UserIdentifier(UserIdentifier.Type.ROLE, producer.getIdentifier());
    }

    private UserIdentifier getConsumerIdentifier() {
        RoleContainer.RoleMode roleMode = consumer.getMode();

        if (roleMode == RoleContainer.RoleMode.EXISTING_USER) {
            return new UserIdentifier(UserIdentifier.Type.USER, consumer.getIdentifier());
        } else if (roleMode == RoleContainer.RoleMode.PRODUCER_USER) {
            return UserIdentifier.getProducer();
        } else if (roleMode == RoleContainer.RoleMode.ANONYMOUS) {
            return UserIdentifier.getAnonymous();
        } else if (roleMode == RoleContainer.RoleMode.NEW_WITH_PRODUCER_ROLE) {
            if (producer.getMode() != RoleContainer.RoleMode.SET_USER_ROLE) {
                throw new IllegalStateException("Cannot use NEW_WITH_PRODUCER_ROLE when producer doesn't have role");
            }
            return new UserIdentifier(UserIdentifier.Type.ROLE, producer.getIdentifier());
        } else {
            return new UserIdentifier(UserIdentifier.Type.ROLE, consumer.getIdentifier());
        }
    }
}
