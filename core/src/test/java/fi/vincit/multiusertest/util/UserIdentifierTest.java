package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
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
    public void testEquals_Producer() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.PRODUCER, null);
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.PRODUCER, null);

        assertThat(userIdentifier1.equals(userIdentifier2), is(true));
    }

    @Test
    public void testEquals_WithProducerRole() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.WITH_PRODUCER_ROLE, null);
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.WITH_PRODUCER_ROLE, null);

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
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.PRODUCER, null);
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.PRODUCER, "Foo");

        assertThat(userIdentifier1.equals(userIdentifier2), is(false));
    }

    @Test
    public void testHashCode_Equals() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.PRODUCER, "Foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.PRODUCER, "Foo");

        assertThat(userIdentifier1.hashCode(), is(userIdentifier2.hashCode()));
    }

    @Test
    public void testHashCode_notEqual_Identifier() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.PRODUCER, "Foo1");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.PRODUCER, "Foo2");

        assertThat(userIdentifier1.hashCode(), is(not(userIdentifier2.hashCode())));
    }

    @Test
    public void testHashCode_notEqual_Type() {
        UserIdentifier userIdentifier1 = new UserIdentifier(UserIdentifier.Type.USER, "Foo");
        UserIdentifier userIdentifier2 = new UserIdentifier(UserIdentifier.Type.PRODUCER, "Foo");

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
    public void testToString_WithProducerRole() {
        assertThat(UserIdentifier.getWithProducerRole().toString(), is("with_producer_role"));
    }

    @Test
    public void testToString_Producer() {
        assertThat(UserIdentifier.getProducer().toString(), is("producer"));
    }

    @Test
    public void testParseWithProducerRole() {
        UserIdentifier identifier = UserIdentifier.parse(RunWithUsers.WITH_PRODUCER_ROLE);

        assertThat(identifier.getType(), is(UserIdentifier.Type.WITH_PRODUCER_ROLE));
        assertThat(identifier.getIdentifier(), nullValue());
    }

    @Test
    public void testParseProducer() {
        UserIdentifier identifier = UserIdentifier.parse(RunWithUsers.PRODUCER);

        assertThat(identifier.getType(), is(UserIdentifier.Type.PRODUCER));
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
