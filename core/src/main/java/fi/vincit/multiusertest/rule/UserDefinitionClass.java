package fi.vincit.multiusertest.rule;

/**
 * Defines
 */
public interface UserDefinitionClass {

    String[] getUsers();

    default boolean hasUsers() {
        return getUsers() != null && getUsers().length > 0;
    }

}
