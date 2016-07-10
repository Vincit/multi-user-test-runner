package fi.vincit.multiusertest.test;

/**
 * Interface for role converters
 * @param <ROLE> Role type in the system under test
 */
public interface RoleConverter<ROLE>  {

    /**
     * Returns given role string as system role object/enum.
     * @param role Role as string.
     * @return Role object/enum
     */
    ROLE stringToRole(String role);

}
