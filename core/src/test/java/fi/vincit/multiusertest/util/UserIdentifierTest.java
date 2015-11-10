package fi.vincit.multiusertest.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import fi.vincit.multiusertest.annotation.RunWithUsers;

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
    public void testEquals_SameIdentifierDifferentType() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.USER, "foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.ROLE, "foo");

        assertThat(userIdentifier1.equals(userIdentifier2), is(false));
    }

    @Test
    public void testEquals_DifferentIdentifierSameType() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.ROLE, "Foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.ROLE, "foo");

        assertThat(userIdentifier1.equals(userIdentifier2), is(false));
    }

    @Test
    public void testEquals_SameObject() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.ROLE, "Foo");

        assertThat(userIdentifier1.equals(userIdentifier1), is(true));
    }

    @Test
    public void testEquals_WrongClass() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.ROLE, "Foo");

        assertThat(userIdentifier1.equals(1L), is(false));
    }

    @Test
    public void testEquals_NullIdentifierFails() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.CREATOR, null);
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.CREATOR, "Foo");

        assertThat(userIdentifier1.equals(userIdentifier2), is(false));
    }

    @Test
    public void testHashCode_Equals() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.CREATOR, "Foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.CREATOR, "Foo");

        assertThat(userIdentifier1.hashCode(), is(userIdentifier2.hashCode()));
    }

    @Test
    public void testHashCode_notEqual_Identifier() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.CREATOR, "Foo1");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.CREATOR, "Foo2");

        assertThat(userIdentifier1.hashCode(), is(not(userIdentifier2.hashCode())));
    }

    @Test
    public void testHashCode_notEqual_Type() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.USER, "Foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.CREATOR, "Foo");

        assertThat(userIdentifier1.hashCode(), is(not(userIdentifier2.hashCode())));
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

    @Test
    public void testParseNewUser() {
        UserIdentifier identifier = UserIdentifier.parse(RunWithUsers.WITH_PRODUCER_ROLE);

        assertThat(identifier.getType(), is(UserIdentifier.Type.NEW_USER));
        assertThat(identifier.getIdentifier(), nullValue());
    }

    @Test
    public void testParseCreator() {
        UserIdentifier identifier = UserIdentifier.parse(RunWithUsers.PRODUCER);

        assertThat(identifier.getType(), is(UserIdentifier.Type.CREATOR));
        assertThat(identifier.getIdentifier(), nullValue());
    }

    @Test
    public void testParseRole() {
        UserIdentifier identifier = UserIdentifier.parse("role:Foo");

        assertThat(identifier.getType(), is(UserIdentifier.Type.ROLE));
        assertThat(identifier.getIdentifier(), is("Foo"));
    }

    @Test
    public void testParseUser() {
        UserIdentifier identifier = UserIdentifier.parse("user:Bar");

        assertThat(identifier.getType(), is(UserIdentifier.Type.USER));
        assertThat(identifier.getIdentifier(), is("Bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParse_IllegalType() {
        UserIdentifier.parse("foo:bar");
    }
}
