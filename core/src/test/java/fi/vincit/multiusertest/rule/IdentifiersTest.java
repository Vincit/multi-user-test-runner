package fi.vincit.multiusertest.rule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fi.vincit.multiusertest.annotation.RunWithUsers;

public class IdentifiersTest {

    @Test
    public void testToString() throws Exception {
        assertThat(
                Identifiers.of(
                    "role:ROLE_USER",
                    "user:username",
                    RunWithUsers.WITH_PRODUCER_ROLE,
                    RunWithUsers.PRODUCER,
                    RunWithUsers.ANONYMOUS).toString(),
                is("role:ROLE_USER, user:username, new user, creator, anonymous")
        );
    }
}