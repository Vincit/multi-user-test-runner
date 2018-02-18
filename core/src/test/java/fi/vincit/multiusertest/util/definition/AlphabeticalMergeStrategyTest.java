package fi.vincit.multiusertest.util.definition;

import fi.vincit.multiusertest.util.merge.AlphabeticalMergeStrategy;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AlphabeticalMergeStrategyTest {

    private static final AlphabeticalMergeStrategy SUT = new AlphabeticalMergeStrategy();

    @Test
    public void resolveDefinitions() {
        final String[] roles = SUT.mergeDefinitions(
                new String[0],
                new String[]{"role:A"}
        );

        assertThat(roles, is(new String[] {"role:A"}));
    }

    @Test
    public void resolveDefinitions_empty() {
        final String[] roles = SUT.mergeDefinitions(
                new String[0],
                new String[0]
        );

        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_nulls() {
        final String[] roles = SUT.mergeDefinitions(
                null,
                null
        );
        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_nulls_definitionReturnsNull() {
        final String[] roles = SUT.mergeDefinitions(
                null,
                null
        );
        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_nulls_definitionReturnsEmpty() {
        final String[] roles = SUT.mergeDefinitions(
                null,
                new String[0]
        );
        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_empty_definitionReturnsEmpty() {
        final String[] roles = SUT.mergeDefinitions(
                new String[0],
                new String[0]
        );
        assertThat(roles, is(new String[0]));
    }

    @Test
    public void resolveDefinitions_bothDefined() {
        final String[] roles = SUT.mergeDefinitions(
                new String[] {"role:A"},
                new String[] {"role:B"}
        );
        assertThat(roles, is(new String[] {"role:A", "role:B"}));
    }

}