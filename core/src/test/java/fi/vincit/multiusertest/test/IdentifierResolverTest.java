package fi.vincit.multiusertest.test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.RoleContainer;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IdentifierResolverTest {

    public static enum Role {
        ROLE1,
        ROLE2
    }

    private static RoleConverter<Role> resolver = role ->
            Role.valueOf(role.toUpperCase());

    @Test
    public void userRole() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.parse("role:role1"), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.parse("role:role2"), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.parse("role:role2")));
    }

    @Test
    public void producerRole() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.parse("role:role1"), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.getProducer(), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.getProducer()));
    }

    @Test
    public void consumerRole() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.parse("user:user1"), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.getProducer(), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.getProducer()));
    }

    @Test
    public void existingUser() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.parse("user:user1"), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.parse("user:user2"), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.parse("user:user2")));
    }

    @Test
    public void anonymous() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.getAnonymous(), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.getAnonymous(), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.getAnonymous()));
    }

    @Test
    public void newUserWithProducerRole() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.parse("role:role1"), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.getWithProducerRole(), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        assertThat(resolver.getIdentifierFor(LoginRole.CONSUMER), is(UserIdentifier.parse("role:role1")));
    }

    @Test(expected = IllegalStateException.class)
    public void newUserWithProducerRole_failsBecauseProducerHasNoRole() {
        RoleContainer producer = RoleContainer.forProducer(UserIdentifier.parse("user:user1"), resolver);
        RoleContainer consumer = RoleContainer.forConsumer(UserIdentifier.getWithProducerRole(), producer, resolver);

        IdentifierResolver resolver = new IdentifierResolver(mockUserResolver(producer, consumer));

        assertThat(resolver.getIdentifierFor(LoginRole.PRODUCER), is(UserIdentifier.getProducer()));
        resolver.getIdentifierFor(LoginRole.CONSUMER);
    }



    private UserResolver mockUserResolver(RoleContainer producer, RoleContainer consumer) {
        UserResolver userResolver = mock(UserResolver.class);
        when(userResolver.getProducer()).thenReturn(producer);
        when(userResolver.getConsumer()).thenReturn(consumer);
        return userResolver;
    }


}