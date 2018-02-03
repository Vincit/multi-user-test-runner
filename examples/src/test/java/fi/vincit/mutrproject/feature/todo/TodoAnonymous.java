package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.UserDefinitionClass;

public class TodoAnonymous implements UserDefinitionClass {

    @Override
    public String[] getUsers() {
        return new String[] {RunWithUsers.ANONYMOUS};
    }
}
