package fi.vincit.multiusertest.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;

public class IdentifierResolverTest {

    @Ignore
    @Test
    public void userRole() {
        TestUser creator = TestUser.forRole("role", UserIdentifier.parse("role:role1"));
        TestUser user = TestUser.forRole("role", UserIdentifier.parse("role:role2"));

        IdentifierResolver resolver = new IdentifierResolver(user, creator);

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role2")));
    }

    @Ignore
    @Test
    public void creator() {
        TestUser creator = TestUser.forRole("role", UserIdentifier.parse("role:role1"));
        TestUser user = TestUser.forRole("role", UserIdentifier.getCreator());

        IdentifierResolver resolver = new IdentifierResolver(user, creator);

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role1")));
    }

}