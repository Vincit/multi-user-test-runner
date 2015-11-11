package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;

public class IdentifierResolver<USER, ROLE> {

    private RoleContainer<ROLE> consumer;
    private RoleContainer<ROLE> producer;

    public IdentifierResolver(UserResolver<USER, ROLE> userResolver) {
        this.consumer = userResolver.getConsumer();
        this.producer = userResolver.getProducer();
    }

    public UserIdentifier getIdentifierFor(LoginRole loginRole) {
        if (loginRole == LoginRole.PRODUCER) {
            return getCreatorIdentifier();
        } else {
            return getUserIdentifier();
        }
    }

    private UserIdentifier getUserIdentifier() {
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

    private UserIdentifier getCreatorIdentifier() {
        return UserIdentifier.getProducer();
    }
}
