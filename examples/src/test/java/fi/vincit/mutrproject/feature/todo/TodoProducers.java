package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.rule.UserDefinitionClass;

public class TodoProducers implements UserDefinitionClass {

    @Override
    public String[] getUsers() {
        return new String[] {
                "role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER", "role:ROLE_USER"
        };
    }
}
