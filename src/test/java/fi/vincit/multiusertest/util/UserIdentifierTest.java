package fi.vincit.multiusertest.util;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserIdentifierTest {

    @Test
    public void testEquals_Role() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.ROLE, "foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.ROLE, "foo");

        assertThat(userIdentifier1.equals(userIdentifier2), is(true));
    }

    @Test
    public void testEquals_User() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.USER, "Foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.USER, "Foo");

        assertThat(userIdentifier1.equals(userIdentifier2), is(true));
    }

    @Test
    public void testEquals_Creator() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.CREATOR, null);
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.CREATOR, null);

        assertThat(userIdentifier1.equals(userIdentifier2), is(true));
    }

    @Test
    public void testEquals_NewUser() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.NEW_USER, null);
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.NEW_USER, null);

        assertThat(userIdentifier1.equals(userIdentifier2), is(true));
    }

    @Test
    public void testToString_Role() {
        assertThat(new UserIdentifier(UserIdentifier.Type.ROLE, "Foo").toString(), is("role:Foo"));
    }

    @Test
    public void testToString_User() {
        assertThat(new UserIdentifier(UserIdentifier.Type.USER, "Foo").toString(), is("user:Foo"));
    }

    @Test
    public void testToString_NewUser() {
        assertThat(UserIdentifier.getNewUser().toString(), is("new_user"));
    }

    @Test
    public void testToString_Creator() {
        assertThat(UserIdentifier.getCreator().toString(), is("creator"));
    }
}
