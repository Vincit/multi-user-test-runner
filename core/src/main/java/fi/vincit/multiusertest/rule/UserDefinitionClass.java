package fi.vincit.multiusertest.rule;

/**
 * Defines a set of user definitions to be used in a test case. Helps reducing
 * duplicated user definitions.
 */
public interface UserDefinitionClass {

    String[] getUsers();

}
