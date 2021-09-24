package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.test.RoleConverter;

/**
 * Utility class for linking system roles and user identifiers.
 * @param <ROLE> Type of the user role in the system under test
 */
public class RoleContainer<ROLE> {

    static <ROLE> RoleContainer<ROLE> forProducerUser() {
        return new RoleContainer<>(null, RoleMode.PRODUCER_USER, UserIdentifier.getProducer());
    }

    static <ROLE> RoleContainer<ROLE> forUserWithProducerRole(ROLE role, UserIdentifier identifier) {
        return new RoleContainer<>(role, RoleMode.NEW_WITH_PRODUCER_ROLE, identifier);
    }

    static <ROLE> RoleContainer<ROLE> forRole(ROLE role, UserIdentifier identifier) {
        return new RoleContainer<>(role, RoleMode.SET_USER_ROLE, identifier);
    }

    static <ROLE> RoleContainer<ROLE> forExistingUser(UserIdentifier identifier) {
        return new RoleContainer<>(null, RoleMode.EXISTING_USER, identifier);
    }

    static <ROLE> RoleContainer<ROLE> forAnonymousUser() {
        return new RoleContainer<>(null, RoleMode.ANONYMOUS, UserIdentifier.getAnonymous());
    }

    public static <ROLE> RoleContainer<ROLE> forProducer(UserIdentifier identifier, RoleConverter<ROLE> roleConverter) {
        if (identifier.getType() == UserIdentifier.Type.USER) {
            return RoleContainer.forExistingUser(identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return RoleContainer.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return RoleContainer.forRole(
                    roleConverter.stringToRole(identifier.getIdentifier()),
                    identifier
            );
        } else {
            throw new IllegalArgumentException("Invalid identifier for producer: " + identifier.getType());
        }
    }

    public static <ROLE> RoleContainer<ROLE> forConsumer(UserIdentifier identifier, RoleContainer<ROLE> producerRoleContainer, RoleConverter<ROLE> roleConverter) {
        if (identifier.getType() == UserIdentifier.Type.PRODUCER) {
            return RoleContainer.forProducerUser();
        } else if (identifier.getType() == UserIdentifier.Type.WITH_PRODUCER_ROLE) {
            if (producerRoleContainer.getMode() == RoleContainer.RoleMode.EXISTING_USER) {
                throw new IllegalStateException("Cannot use WITH_PRODUCER_ROLE mode when producer uses existing user");
            }
            return RoleContainer.forUserWithProducerRole(producerRoleContainer.getRole(), identifier);
        } else if (identifier.getType() == UserIdentifier.Type.ANONYMOUS) {
            return RoleContainer.forAnonymousUser();
        } else if (identifier.getType() == UserIdentifier.Type.ROLE) {
            return RoleContainer.forRole(roleConverter.stringToRole(identifier.getIdentifier()), identifier);
        } else {
            return RoleContainer.forExistingUser(identifier);
        }
    }

    public enum RoleMode {
        PRODUCER_USER,
        NEW_WITH_PRODUCER_ROLE,
        SET_USER_ROLE,
        EXISTING_USER,
        ANONYMOUS
    }

    private final ROLE role;
    private final RoleMode mode;
    private final UserIdentifier userIdentifier;


    RoleContainer(ROLE role, RoleMode mode, UserIdentifier userIdentifier) {
        this.role = role;
        this.mode = mode;
        this.userIdentifier = userIdentifier;
    }

    public ROLE getRole() {
        assert role != null : "Role must not be null";
        return role;
    }

    public RoleMode getMode() {
        assert mode != null : "Mode must not be null";
        return mode;
    }

    public FocusType getFocusMode() {
        assert userIdentifier != null : "UserIdentifier must not be null";
        return userIdentifier.getFocusMode();
    }


    public UserIdentifier getUserIdentifier() {
        assert userIdentifier != null : "UserIdentifier must not be null";
        return userIdentifier;
    }
    
    public String getIdentifier() {
        return getUserIdentifier().getIdentifier();
    }
}
