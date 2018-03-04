package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * Utility class for resolving user from user identifiers.
 * Creates new users to the system if necessary.
 * @param <USER> User type in the system under test
 * @param <ROLE> Role type in the system under test
 */
public class UserResolver<USER, ROLE> {

    private final RoleContainer<ROLE> producerRoleContainer;
    private USER producer;

    private final RoleContainer<ROLE> consumerRoleContainer;
    private USER consumer;

    private final UserFactory<USER, ROLE> userFactory;

    /**
     * Initializes the resolver. Calling {@link #resolve()} will actually
     * resolve the users and creates them if necessary.
     * @param userFactory User factory used to create new users
     * @param roleConverter Role converter class
     * @param producer Producer identifier
     * @param consumer Consumer identifier
     */
    public UserResolver(UserFactory<USER, ROLE> userFactory, RoleConverter<ROLE> roleConverter, UserIdentifier producer, UserIdentifier consumer) {
        this.userFactory = userFactory;
        this.producerRoleContainer = RoleContainer.forProducer(producer, roleConverter);
        this.consumerRoleContainer = RoleContainer.forConsumer(consumer, producerRoleContainer, roleConverter);
    }

    private void initializeConsumer() {
        if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.SET_USER_ROLE) {
            consumer = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Consumer", consumerRoleContainer.getRole(), LoginRole.CONSUMER);
        } else if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.PRODUCER_USER) {
            if (producerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
                // Do nothing, resolved in getter
            } else {
                consumer = producer;
            }
        } else if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.NEW_WITH_PRODUCER_ROLE) {
            if (producerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
                // NOOP
            } else {
                consumer = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Consumer", producerRoleContainer.getRole(), LoginRole.CONSUMER);
            }
        } else if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            // Do nothing, resolved in getter
        } else if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            // Do nothing, consumer is not used
        } else {
            throw new IllegalArgumentException("Invalid consumer mode: " + consumerRoleContainer.getMode());
        }
    }

    private void initializeProducer() {
        if (producerRoleContainer.getMode() == RoleContainer.RoleMode.SET_USER_ROLE) {
            producer = userFactory.createUser(userFactory.getRandomUsername(), "Test", "Producer", producerRoleContainer.getRole(), LoginRole.PRODUCER);
        } else if (producerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            // Do nothing, resolved in getter
        } else if (producerRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            // Do nothing, consumer is not used
        } else {
            throw new IllegalArgumentException("Invalid producer consumer mode: " + producerRoleContainer.getMode());
        }
    }

    public RoleContainer<ROLE> getProducer() {
        return producerRoleContainer;
    }

    public RoleContainer<ROLE> getConsumer() {
        return consumerRoleContainer;
    }

    /**
     * Returns consumer user to use. {@link #resolve()} must be called before.
     * @return Consumer user instance
     */
    public USER resolveConsumer() {
        if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(consumerRoleContainer.getIdentifier());
        } else if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            return null;
        } else if (consumerRoleContainer.getMode() == RoleContainer.RoleMode.PRODUCER_USER) {
            return resolverProducer();
        } else {
            return consumer;
        }
    }

    /**
     * Returns producer user to use. {@link #resolve()} must be called before.
     * @return Producer user instance
     */
    public USER resolverProducer() {
        if (producerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
            return userFactory.getUserByUsername(producerRoleContainer.getIdentifier());
        } else if (producerRoleContainer.getMode() == RoleContainer.RoleMode.ANONYMOUS) {
            return null;
        } else {
            return producer;
        }
    }

    /**
     * Resolves the producer and consumer users. If user needs to be created
     * creates one.
     */
    public void resolve() {
        initializeProducer();
        initializeConsumer();
    }
}
