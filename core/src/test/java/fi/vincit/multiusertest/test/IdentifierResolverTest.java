package fi.vincit.multiusertest.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.TestUser;
import fi.vincit.multiusertest.util.UserIdentifier;

public class IdentifierResolverTest {

    public static enum Role {
        ROLE1,
        ROLE2
    }

    @Test
    public void userRole() {
        TestUser creator = TestUser.forRole(Role.ROLE1, UserIdentifier.parse("role:role1"));
        TestUser user = TestUser.forRole(Role.ROLE2, UserIdentifier.parse("role:role2"));

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role2")));
    }

    @Test
    public void creator_role() {
        TestUser creator = TestUser.forRole(Role.ROLE1, UserIdentifier.parse("role:role1"));
        TestUser user = TestUser.forCreatorUser();

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.getCreator()));
    }

    @Test
    public void creator_user() {
        TestUser creator = TestUser.forExistingUser(UserIdentifier.parse("user:user1"));
        TestUser user = TestUser.forCreatorUser();

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.getCreator()));
    }

    @Test
    public void existingUser() {
        TestUser creator = TestUser.forExistingUser(UserIdentifier.parse("user:user1"));
        TestUser user = TestUser.forExistingUser(UserIdentifier.parse("user:user2"));

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("user:user2")));
    }

    @Test
    public void anonymous() {
        TestUser creator = TestUser.forAnonymousUser();
        TestUser user = TestUser.forAnonymousUser();

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.getAnonymous()));
    }

    @Test
    public void newUserWithCreatorRole() {
        TestUser creator = TestUser.forRole(Role.ROLE1, UserIdentifier.parse("role:role1"));
        TestUser user = TestUser.forNewUser(Role.ROLE1, UserIdentifier.getNewUser());

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role1")));
    }

    @Test(expected = IllegalStateException.class)
    public void newUserWithCreatorRole_failsBecauseCreatorHasNoRole() {
        TestUser creator = TestUser.forExistingUser(UserIdentifier.parse("user:user1"));
        TestUser user = TestUser.forNewUser(null, UserIdentifier.getNewUser());

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        resolver.getIdentifierFor(LoginRole.USER);
    }



    private UserResolver mockUserResolver(TestUser creator, TestUser user) {
        UserResolver userResolver = mock(UserResolver.class);
        when(userResolver.getCreator()).thenReturn(creator);
        when(userResolver.getUser()).thenReturn(user);
        return userResolver;
    }


}