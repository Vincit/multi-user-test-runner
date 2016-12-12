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


    @Override
    public Collection<ROLE> stringToRole(String role) {
        return UserIdentifier.mapMultiRoleIdentifier(role, this::identifierPartToRole);
    }

    /**
     * Maps single multi-role role part to a role.
     * @param identifier Multi-role identifier part
     * @return Part as role
     */
    protected abstract ROLE identifierPartToRole(String identifier);

}
