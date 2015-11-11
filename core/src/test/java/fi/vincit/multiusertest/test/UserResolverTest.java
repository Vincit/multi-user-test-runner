package fi.vincit.multiusertest.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;

public class UserResolverTest {

    @Test
    public void resolveRoles() {

        RoleConverter<String> roleConverter = mock(RoleConverter.class);
        when(roleConverter.stringToRole(anyString())).thenReturn("role");

        UserFactory<String, String> factory = mockFactory("producer", "consumer");

        UserResolver<String, String> resolver = new UserResolver<>(
                factory,
                roleConverter,
                UserIdentifier.parse("role:role1"),
                UserIdentifier.parse("role:role2")
        );

        resolver.resolve();

        assertThat(resolver.resolverProducer(), is("producer"));
        assertThat(resolver.resolveConsumer(), is("consumer"));

        verify(factory, never()).getUserByUsername(anyString());

    }

    @Test
    public void resolveProducersAndConsumers() {

        RoleConverter<String> roleConverter = mock(RoleConverter.class);

        UserFactory<String, String> factory = mockFactoryExisting("user_producer", "user_consumer");

        UserResolver<String, String> resolver = new UserResolver<>(
                factory,
                roleConverter,
                UserIdentifier.parse("user:user1"),
                UserIdentifier.parse("user:user2")
        );

        resolver.resolve();

        assertThat(resolver.resolverProducer(), is("user_producer"));
        assertThat(resolver.resolveConsumer(), is("user_consumer"));

        verify(factory).getUserByUsername("user1");
        verify(factory).getUserByUsername("user2");
        verify(factory, never()).createUser(anyString(), anyString(), anyString(), anyString(), any(LoginRole.class));


    }

    @Test
    public void resolveAnonymous() {

        RoleConverter<String> roleConverter = mock(RoleConverter.class);

        UserFactory<String, String> factory = mockFactoryExisting("user_producer", "user_consumer");

        UserResolver<String, String> resolver = new UserResolver<>(
                factory,
                roleConverter,
                UserIdentifier.getAnonymous(),
                UserIdentifier.getAnonymous()
        );

        resolver.resolve();

        assertThat(resolver.resolverProducer(), nullValue());
        assertThat(resolver.resolveConsumer(), nullValue());

        verify(factory, never()).getUserByUsername(anyString());
        verify(factory, never()).createUser(anyString(), anyString(), anyString(), anyString(), any(LoginRole.class));
    }

    @Test(expected = IllegalStateException.class)
    public void errorWhenProducerExistingAndConsumerCreatorRole() {

        RoleConverter<String> roleConverter = mock(RoleConverter.class);

        UserFactory<String, String> factory = mockFactoryExisting("user_producer", "user_consumer");

        UserResolver<String, String> resolver = new UserResolver<>(
                factory,
                roleConverter,
                UserIdentifier.parse("user:user1"),
                UserIdentifier.getWithProducerRole()
        );

        resolver.resolve();
    }

    @Test
    public void resolveProducerAsConsumer() {

        RoleConverter<String> roleConverter = mock(RoleConverter.class);
        when(roleConverter.stringToRole(anyString())).thenReturn("role");

        UserFactory<String, String> factory = mockFactoryExisting("producer_user", "user_consumer");

        UserResolver<String, String> resolver = new UserResolver<>(
                factory,
                roleConverter,
                UserIdentifier.parse("user:user1"),
                UserIdentifier.getProducer()
        );

        resolver.resolve();

        assertThat(resolver.resolverProducer(), is("producer_user"));
        assertThat(resolver.resolveConsumer(), is("producer_user"));

        verify(factory, times(2)).getUserByUsername("user1");

    }

    private UserFactory<String, String> mockFactory(String producer, String consumer) {
        UserFactory<String, String> factory = mock(UserFactory.class);
        when(factory.createUser(anyString(), anyString(), anyString(), anyString(), eq(LoginRole.PRODUCER)))
            .thenReturn(producer);
        when(factory.createUser(anyString(), anyString(), anyString(), anyString(), eq(LoginRole.CONSUMER)))
                .thenReturn(consumer);
        return factory;
    }

    private UserFactory<String, String> mockFactoryExisting(String producer, String consumer) {
        UserFactory<String, String> factory = mock(UserFactory.class);
        when(factory.getUserByUsername("user1")).thenReturn(producer);
        when(factory.getUserByUsername("user2")).thenReturn(consumer);
        return factory;
    }

    private UserFactory<String, String> mockFactoryEmpty() {
        return mock(UserFactory.class);
    }

}