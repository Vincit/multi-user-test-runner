package fi.vincit.multiusertest.rule;

/*
    Default implementation used if no user definition class is defined.
    Return an empty array.
 */
public class EmptyUserDefinitionClass implements UserDefinitionClass {

    private static final String[] EMPTY_DEFINITIONS = new String[0];

    private static final UserDefinitionClass EMPTY_DEFINITION_CLASS = new EmptyUserDefinitionClass();

    public static UserDefinitionClass getEmptyClass() {
        return EMPTY_DEFINITION_CLASS;
    }

    @Override
    public String[] getUsers() {
        return EMPTY_DEFINITIONS;
    }
}
