package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Collection;

/**
 * Default configuration base class for multi user and role tests. Authorization rule is automatically set
 * by the runner.
 * @since 0.5
 * @param <USER> User type
 * @param <ROLE> Role type
 */
public abstract class AbstractMultiUserAndRoleConfig<USER, ROLE> extends AbstractMultiUserConfig<USER,Collection<ROLE>> {

    /**
     * Default constructor
     */
    public AbstractMultiUserAndRoleConfig() {
    }

    /**
     * Transforms the given role to a collection of roles using {@link AbstractMultiUserAndRoleConfig#identifierPartToRole(String)}
     * method.
     * @param role Role as string.
     * @return Collection of roles
     */
    @Override
    public Collection<ROLE> stringToRole(String role) {
        return UserIdentifier.mapMultiRoleIdentifier(role, this::identifierPartToRole);
    }

    /**
     * Maps single multi-role role part to a role. By default, throws an exception, so it must
     * be implemented in case the default {@link AbstractMultiUserAndRoleConfig#stringToRole(String)} method is used.
     * @param identifier Multi-role identifier part
     * @return Part as role
     */
    protected ROLE identifierPartToRole(String identifier) {
        throw new UnsupportedOperationException("Please implement. This method");
    }

}
