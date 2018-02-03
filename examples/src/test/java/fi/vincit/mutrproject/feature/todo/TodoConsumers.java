package fi.vincit.mutrproject.feature.todo;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.UserDefinitionClass;

public class TodoConsumers implements UserDefinitionClass {

    @Override
    public String[] getUsers() {
        return new String[] {
                "role:ROLE_SYSTEM_ADMIN", "role:ROLE_ADMIN", "role:ROLE_USER",
                RunWithUsers.PRODUCER, RunWithUsers.ANONYMOUS
        };
    }
}
