package fi.vincit.multiusertest.util;

import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;

public class UserIdentifiersTest {

    @Test
    public void testIfAnyOf_String() {
        UserIdentifiers userIdentifiers = UserIdentifiers.ifAnyOf("role:ROLE_USER", "user:foo");

        assertThat(userIdentifiers.getIdentifiers(),
                is(Arrays.asList(
                    UserIdentifier.parse("role:ROLE_USER"),
                    UserIdentifier.parse("user:foo")
                ))
        );
    }

    @Test
    public void testIfAnyOf_RolesHelper() {
        UserIdentifiers userIdentifiers = UserIdentifiers.ifAnyOf(roles("ROLE_USER", "ROLE_ADMIN"));

        assertThat(userIdentifiers.getIdentifiers(),
                is(Arrays.asList(
                        UserIdentifier.parse("role:ROLE_USER"),
                        UserIdentifier.parse("role:ROLE_ADMIN")
                ))
        );
    }

    @Test
    public void testIfAnyOf_UsersHelper() {
        UserIdentifiers userIdentifiers = UserIdentifiers.ifAnyOf(users("User1", "User2", "User3"));

        assertThat(userIdentifiers.getIdentifiers(),
                is(Arrays.asList(
                        UserIdentifier.parse("user:User1"),
                        UserIdentifier.parse("user:User2"),
                        UserIdentifier.parse("user:User3")
                ))
        );
    }

    @Test
    public void testIfAnyOf_UsersAndRolesHelper() {
        UserIdentifiers userIdentifiers = UserIdentifiers.ifAnyOf(roles("ROLE_USER", "ROLE_ADMIN"), users("User1", "User2", "User3"));

        assertThat(userIdentifiers.getIdentifiers(),
                is(Arrays.asList(
                        UserIdentifier.parse("role:ROLE_USER"),
                        UserIdentifier.parse("role:ROLE_ADMIN"),
                        UserIdentifier.parse("user:User1"),
                        UserIdentifier.parse("user:User2"),
                        UserIdentifier.parse("user:User3")
                ))
        );
    }

}