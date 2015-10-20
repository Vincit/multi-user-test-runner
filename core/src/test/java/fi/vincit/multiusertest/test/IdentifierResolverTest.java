package fi.vincit.multiusertest.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role2")));
    }

    private UserResolver mockUserResolver(TestUser creator, TestUser user) {
        UserResolver userResolver = mock(UserResolver.class);
        when(userResolver.getCreator()).thenReturn(creator);
        when(userResolver.getUser()).thenReturn(user);
        return userResolver;
    }

    @Ignore
    @Test
    public void creator() {
        TestUser creator = TestUser.forRole("role", UserIdentifier.parse("role:role1"));
        TestUser user = TestUser.forRole("role", UserIdentifier.getCreator());

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role1")));
    }

}