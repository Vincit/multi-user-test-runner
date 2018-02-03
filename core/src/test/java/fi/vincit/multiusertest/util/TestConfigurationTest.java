package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.EmptyUserDefinitionClass;
import fi.vincit.multiusertest.rule.UserDefinitionClass;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestConfigurationTest {

    @Test
    public void ignoreUsers() {
        RunWithUsers run = mockRunWithUsers(new String[] {"role:A", "role:B", "role:C"}, new String[] {"role:D", "role:E", "role:F"});
        IgnoreForUsers ignore = mockIgnoreForUsers(new String[] {"role:B", "role:D"}, new String[] {"role:B", "role:E"});
        TestConfiguration testConfiguration =
                TestConfiguration.fromIgnoreForUsers(Optional.of(ignore), Optional.of(run));

        assertThat(testConfiguration.getProducerIdentifiers(), is(asSet("role:A", "role:C")));
        assertThat(testConfiguration.getConsumerIdentifiers(), is(asSet("role:D", "role:F")));
    }

    @Test
    public void ignoreUsers_IgnoredDontMatch() {
        RunWithUsers run = mockRunWithUsers(new String[] {"role:A", "role:B", "role:C"}, new String[] {"role:D", "role:E", "role:F"});
        IgnoreForUsers ignore = mockIgnoreForUsers(new String[] {"role:D"}, new String[] {"role:B"});
        TestConfiguration testConfiguration =
                TestConfiguration.fromIgnoreForUsers(Optional.of(ignore), Optional.of(run));

        assertThat(testConfiguration.getProducerIdentifiers(), is(asSet("role:A", "role:B", "role:C")));
        assertThat(testConfiguration.getConsumerIdentifiers(), is(asSet("role:D", "role:E", "role:F")));
    }

    @Test
    public void runWithUsers() {
        RunWithUsers run = mockRunWithUsers(new String[] {"role:A", "role:B", "role:C"}, new String[] {"role:D", "role:E", "role:F"});
        TestConfiguration testConfiguration =
                TestConfiguration.fromRunWithUsers(Optional.of(run), Optional.empty());

        assertThat(testConfiguration.getProducerIdentifiers(), is(asSet("role:A", "role:B", "role:C")));
        assertThat(testConfiguration.getConsumerIdentifiers(), is(asSet("role:D", "role:E", "role:F")));
    }

    @Test
    public void resolveDefinitions() {
        final String[] roles = TestConfiguration.resolveDefinitions(
                new String[0],
                new TestUserDefinitionClass(new String[]{"role:A"})
        );

        assertThat(roles, is(new String[] {"role:A"}));
    }

    @Test
    public void resolveDefinitions_empty() {
        final String[] roles = TestConfiguration.resolveDefinitions(
                new String[0],
                new TestUserDefinitionClass(new String[0])
        );

        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_nulls() {
        final String[] roles = TestConfiguration.resolveDefinitions(null, null);
        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_nulls_definitionReturnsNull() {
        final String[] roles = TestConfiguration.resolveDefinitions(
                null,
                new TestUserDefinitionClass(null)
        );
        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_bothDefined() {
        final String[] roles = TestConfiguration.resolveDefinitions(
                new String[] {"role:A"},
                new TestUserDefinitionClass(new String[] {"role:B"})
        );
        assertThat(roles, is(new String[] {"role:A"}));
    }

    @Test
    public void resolveUserDefinitionClass() {
        final UserDefinitionClass userDefinitionClass = TestConfiguration.resolveUserDefinitionClass(
                TestUserDefinitionClass.class
        );
        assertThat(userDefinitionClass, instanceOf(TestUserDefinitionClass.class));
    }

    @Test
    public void resolveUserDefinitionClass_empty() {
        final UserDefinitionClass userDefinitionClass = TestConfiguration.resolveUserDefinitionClass(null);
        assertThat(userDefinitionClass, instanceOf(EmptyUserDefinitionClass.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveUserDefinitionClass_invalid() {
        TestConfiguration.resolveUserDefinitionClass(
                InvalidUserDefinitionClass.class
        );
    }

    private static Set<UserIdentifier> asSet(String... identifierDefs) {
        return Stream.of(identifierDefs).map(UserIdentifier::parse).collect(Collectors.toSet());
    }

    private RunWithUsers mockRunWithUsers(String[] producers, String[] consumers) {
        RunWithUsers testUsers = mock(RunWithUsers.class);

        when(testUsers.producers()).thenReturn(producers);
        when(testUsers.consumers()).thenReturn(consumers);

        return testUsers;
    }

    private IgnoreForUsers mockIgnoreForUsers(String[] producers, String[] consumers) {
        IgnoreForUsers testUsers = mock(IgnoreForUsers.class);

        when(testUsers.producers()).thenReturn(producers);
        when(testUsers.consumers()).thenReturn(consumers);

        return testUsers;
    }

    public static class TestUserDefinitionClass implements UserDefinitionClass {
        private String[] users;

        public TestUserDefinitionClass() {
        }

        TestUserDefinitionClass(String[] users) {
            this.users = users;
        }

        @Override
        public String[] getUsers() {
            return users;
        }
    }

    /**
     * Test user definition class that can't be initialized with zero argument constructor
     */
    public static class InvalidUserDefinitionClass implements UserDefinitionClass {
        InvalidUserDefinitionClass(String param) {
        }

        @Override
        public String[] getUsers() {
            return null;
        }
    }
}