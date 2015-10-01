package fi.vincit.multiusertest.rule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fi.vincit.multiusertest.annotation.TestUsers;

public class IdentifiersTest {

    @Test
    public void testToString() throws Exception {
        assertThat(
                Identifiers.of(
                    "role:ROLE_USER",
                    "user:username",
                    TestUsers.NEW_USER,
                    TestUsers.CREATOR,
                    TestUsers.ANONYMOUS).toString(),
                is("role:ROLE_USER, user:username, new user, creator, anonymous")
        );
    }
}