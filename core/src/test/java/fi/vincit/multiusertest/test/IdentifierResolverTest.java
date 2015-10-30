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

    @Test
    public void userRole() {
        RoleContainer creator = RoleContainer.forRole(Role.ROLE1, UserIdentifier.parse("role:role1"));
        RoleContainer user = RoleContainer.forRole(Role.ROLE2, UserIdentifier.parse("role:role2"));

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role2")));
    }

    @Test
    public void creator_role() {
        RoleContainer creator = RoleContainer.forRole(Role.ROLE1, UserIdentifier.parse("role:role1"));
        RoleContainer user = RoleContainer.forCreatorUser();

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.getCreator()));
    }

    @Test
    public void creator_user() {
        RoleContainer creator = RoleContainer.forExistingUser(UserIdentifier.parse("user:user1"));
        RoleContainer user = RoleContainer.forCreatorUser();

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.getCreator()));
    }

    @Test
    public void existingUser() {
        RoleContainer creator = RoleContainer.forExistingUser(UserIdentifier.parse("user:user1"));
        RoleContainer user = RoleContainer.forExistingUser(UserIdentifier.parse("user:user2"));

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("user:user2")));
    }

    @Test
    public void anonymous() {
        RoleContainer creator = RoleContainer.forAnonymousUser();
        RoleContainer user = RoleContainer.forAnonymousUser();

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.getAnonymous()));
    }

    @Test
    public void newUserWithCreatorRole() {
        RoleContainer creator = RoleContainer.forRole(Role.ROLE1, UserIdentifier.parse("role:role1"));
        RoleContainer user = RoleContainer.forNewUser(Role.ROLE1, UserIdentifier.getNewUser());

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        assertThat(resolver.getIdentifierFor(LoginRole.USER), is(UserIdentifier.parse("role:role1")));
    }

    @Test(expected = IllegalStateException.class)
    public void newUserWithCreatorRole_failsBecauseCreatorHasNoRole() {
        RoleContainer creator = RoleContainer.forExistingUser(UserIdentifier.parse("user:user1"));
        RoleContainer user = RoleContainer.forNewUser(null, UserIdentifier.getNewUser());

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(creator, user));

        assertThat(resolver.getIdentifierFor(LoginRole.CREATOR), is(UserIdentifier.getCreator()));
        resolver.getIdentifierFor(LoginRole.USER);
    }



    private UserResolver mockUserResolver(RoleContainer creator, RoleContainer user) {
        UserResolver userResolver = mock(UserResolver.class);
        when(userResolver.getCreator()).thenReturn(creator);
        when(userResolver.getUser()).thenReturn(user);
        return userResolver;
    }


}