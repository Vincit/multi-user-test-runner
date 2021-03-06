package fi.vincit.mutrproject.configuration;

import fi.vincit.mutrproject.testconfig.AbstractConfiguredMultiRoleIT;

/**
 * A custom RoleGroup which is used to implement
 * multi role support. See {@link AbstractConfiguredMultiRoleIT}
 * for how to configure multi role support.
 */
public enum RoleGroup {
    ADMINISTRATOR,
    REGULAR_USER
}
