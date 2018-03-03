package fi.vincit.multiusertest.util;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserIdentifiersTest {

    @Test
    public void testIfAnyOf_String() {
        UserIdentifiers userIdentifiers = new UserIdentifiers("role:ROLE_USER", "user:foo");

        assertThat(userIdentifiers.getIdentifiers(),
                is(Arrays.asList(
                    UserIdentifier.parse("role:ROLE_USER"),
                    UserIdentifier.parse("user:foo")
                ))
        );
    }

}