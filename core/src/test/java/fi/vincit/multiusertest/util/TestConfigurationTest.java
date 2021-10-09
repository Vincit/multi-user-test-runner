package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.IgnoreForUsers;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.rule.EmptyUserDefinitionClass;
import fi.vincit.multiusertest.rule.UserDefinitionClass;
import fi.vincit.multiusertest.util.merge.AlphabeticalMergeStrategy;
import org.junit.Test;

import java.util.Collection;
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
                TestConfiguration.fromIgnoreForUsers(ignore, run, Object.class);

        assertThat(testConfiguration.getProducerIdentifiers(), is(asSet("role:A", "role:C")));
        assertThat(testConfiguration.getConsumerIdentifiers(), is(asSet("role:D", "role:F")));
    }

    @Test
    public void ignoreUsers_IgnoredDontMatch() {
        RunWithUsers run = mockRunWithUsers(new String[] {"role:A", "role:B", "role:C"}, new String[] {"role:D", "role:E", "role:F"});
        IgnoreForUsers ignore = mockIgnoreForUsers(new String[] {"role:D"}, new String[] {"role:B"});
        TestConfiguration testConfiguration =
                TestConfiguration.fromIgnoreForUsers(ignore, run, Object.class);

        assertThat(testConfiguration.getProducerIdentifiers(), is(asSet("role:A", "role:B", "role:C")));
        assertThat(testConfiguration.getConsumerIdentifiers(), is(asSet("role:D", "role:E", "role:F")));
    }

    @Test
    public void runWithUsers() {
        RunWithUsers run = mockRunWithUsers(new String[] {"role:A", "role:B", "role:C"}, new String[] {"role:D", "role:E", "role:F"});
        TestConfiguration testConfiguration =
                TestConfiguration.fromRunWithUsers(run, null, Object.class);

        assertThat(testConfiguration.getProducerIdentifiers(), is(asSet("role:A", "role:B", "role:C")));
        assertThat(testConfiguration.getConsumerIdentifiers(), is(asSet("role:D", "role:E", "role:F")));
    }

    @Test
    public void getDefinitions() {
        final Collection<UserIdentifier> definitions = TestConfiguration.getDefinitions(
                new String[] {"role:A"},
                new String[] {"role:B"},
                new AlphabeticalMergeStrategy(),
                FocusType.NONE
        );

        assertThat(definitions, is(asSet("role:A", "role:B")));
    }

    @Test
    public void getDefinitions_OnlyFocus() {
        final Collection<UserIdentifier> definitions = TestConfiguration.getDefinitions(
                new String[] {"role:A"},
                new String[] {"$role:B"},
                new AlphabeticalMergeStrategy(),
                FocusType.FOCUS
        );

        assertThat(definitions, is(asSet("role:B")));
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

    @Test
    public void getDefinitions_empty() {
        final Collection<UserIdentifier> definitions = TestConfiguration.getDefinitions(
                null,
                null,
                new AlphabeticalMergeStrategy(),
                FocusType.NONE
        );

        assertThat(definitions.size(), is(0));
    }

    @Test
    public void getDefinitions_empty_OnlyFocus() {
        final Collection<UserIdentifier> definitions = TestConfiguration.getDefinitions(
                null,
                null,
                new AlphabeticalMergeStrategy(),
                FocusType.FOCUS
        );

        assertThat(definitions.size(), is(0));
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
