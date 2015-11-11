package fi.vincit.multiusertest.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;

public class IdentifierResolverTest {

    public static enum Role {
        ROLE1,
        ROLE2
    }

    private static RoleConverter<Role> resolver = new RoleConverter<Role>() {
        @Override
        public Role stringToRole(String role) {
            return Role.valueOf(role.toUpperCase());
        }
    };

    @Test
    public void userRole() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.parse("role:role1"), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.parse("role:role2"), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.parse("role:role2")));
    }

    @Test
    public void creator_role() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.parse("role:role1"), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.getCreator(), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.getCreator()));
    }

    @Test
    public void creator_user() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.parse("user:user1"), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.getCreator(), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.getCreator()));
    }

    @Test
    public void existingUser() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.parse("user:user1"), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.parse("user:user2"), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.parse("user:user2")));
    }

    @Test
    public void anonymous() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.getAnonymous(), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.getAnonymous(), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.getAnonymous()));
    }

    @Test
    public void newUserWithCreatorRole() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.parse("role:role1"), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.getNewUser(), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.parse("role:role1")));
    }

    @Test(expected = IllegalStateException.class)
    public void newUserWithCreatorRole_failsBecauseCreatorHasNoRole() {
        RoleContainer creator = RoleContainer.forCreator(UserIdentifier.parse("user:user1"), resolver);
        RoleContainer user = RoleContainer.forUser(UserIdentifier.getNewUser(), creator, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getCreator()));
        resolver.getIdentifierFor(LoginRole.CONSUMER);
    }



    private UserResolver mockUserResolver(RoleContainer creator, RoleContainer user) {
        UserResolver userResolver = mock(UserResolver.class);
        when(userResolver.getCreator()).thenReturn(creator);
        when(userResolver.getUser()).thenReturn(user);
        return userResolver;
    }


}