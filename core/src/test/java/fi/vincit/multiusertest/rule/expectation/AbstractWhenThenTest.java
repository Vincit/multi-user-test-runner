package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AbstractWhenThenTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private Set<UserIdentifier> setOf(UserIdentifierCollection... identifiers) {
        return Arrays.stream(identifiers)
                .map(UserIdentifierCollection::getUserIdentifiers)
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private static class SUT extends AbstractWhenThen<TestExpectation> {

        public SUT(Set<UserIdentifier> allowedIdentifiers, UserIdentifier producerIdentifier, UserIdentifier userIdentifier) {
            super(producerIdentifier, userIdentifier, mock(AuthorizationRule.class), mock(UserRoleIT.class), allowedIdentifiers);
        }

        @Override
        protected void test(TestExpectation testExpectation, ConsumerProducerSet consumerProducerSet) throws Throwable {
        }

        @Override
        protected TestExpectation getDefaultExpectation(ConsumerProducerSet consumerProducerSet) {
            return null;
        }
    }

    @Test
    public void whenCalledWith_UserIdentifier() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(UserIdentifier.getAnonymous(), UserIdentifier.getProducer()),
                null,
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWithAnyOf(UserIdentifier.getAnonymous(), UserIdentifier.getProducer());

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.getAnonymous(), UserIdentifier.getProducer()))
        );
    }

    @Test
    public void whenCalledWith_UserIdentifier_ThrowIfSameAddedTwice() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(UserIdentifier.getAnonymous(), UserIdentifier.getProducer()),
                null,
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.whenCalledWithAnyOf(UserIdentifier.getAnonymous(), UserIdentifier.getAnonymous());
    }

    @Test
    public void whenCalledWithAnyOf_UserIdentifiers() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(roles("ROLE_ADMIN", "ROLE_USER")),
                null,
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_USER"));

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_ADMIN"), UserIdentifier.parse("role:ROLE_USER")))
        );
    }

    @Test
    public void whenCalledWithAnyOf_UserIdentifiers_InvalidRole() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(roles("ROLE_ADMIN", "ROLE_USER")),
                null,
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(allOf(
                CoreMatchers.containsString("Following identifiers are not valid"),
                CoreMatchers.containsString("role:ROLE_FAKE1"),
                CoreMatchers.containsString("role:ROLE_FAKE2")
        ));
        sut.whenCalledWithAnyOf(roles("ROLE_FAKE1", "ROLE_FAKE2"));
    }

    @Test
    public void whenCalledWithAnyOf_UserIdentifierCollection() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(roles("ROLE_ADMIN", "ROLE_USER")),
                null,
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_USER"));

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_ADMIN"), UserIdentifier.parse("role:ROLE_USER")))
        );
    }

    @Test
    public void whenCalledWithAnyOf_ListOfUserIdentifier() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(roles("ROLE_ADMIN", "ROLE_USER")),
                null,
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWithAnyOf(roles("ROLE_ADMIN", "ROLE_USER").getUserIdentifiers());

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_ADMIN"), UserIdentifier.parse("role:ROLE_USER")))
        );
    }

    @Test
    public void whenCalledWithAnyOf_UserIdentifierSupplier() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(roles("ROLE_ADMIN", "ROLE_USER")),
                null,
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWithAnyOf(() -> roles("ROLE_ADMIN", "ROLE_USER").getUserIdentifiers());

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_ADMIN"), UserIdentifier.parse("role:ROLE_USER")))
        );
    }

    @Test
    public void whenCalledWithAnyOf_UserIdentifierSupplierCollection() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(roles("ROLE_ADMIN", "ROLE_USER"), users("user1")),
                null,
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWithAnyOf(() -> UserIdentifiers.listOf(roles("ROLE_ADMIN", "ROLE_USER"), users("user1")));

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_ADMIN"), UserIdentifier.parse("role:ROLE_USER"), UserIdentifier.parse("user:user1")))
        );
    }

    @Test
    public void then_CalledBeforeWhen_Throws() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(UserIdentifier.getAnonymous()),
                null,
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.then(mock(TestExpectation.class));
    }

    @Test
    public void then_TryingToOverrideSetExpectation_Throws() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(UserIdentifier.getAnonymous()),
                null,
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.whenCalledWithAnyOf(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class))
            .whenCalledWithAnyOf(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class));
    }

    @Test
    public void then_TryingToOverrideSetExpectation_CurrentIdentifiersIsCleared() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(UserIdentifier.getAnonymous()),
                null,
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.whenCalledWithAnyOf(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class))
            .whenCalledWithAnyOf(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class));
        assertThat(sut.getCurrentIdentifiers(), is(setOf()));
    }

    @Test
    public void then_AddsTestExpectationToCurrentIdentifiers_ConsumerOnly() {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        ConsumerProducerSet s1 = new ConsumerProducerSet(null, role1);
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        ConsumerProducerSet s2 = new ConsumerProducerSet(null, role2);
        UserIdentifier role3 = UserIdentifier.parse("role:ROLE_3");
        ConsumerProducerSet s3 = new ConsumerProducerSet(null, role3);

        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(role1, role2, role3),
                null,
                UserIdentifier.getAnonymous()
        );

        TestExpectation testExpectation1 = mock(TestExpectation.class);
        TestExpectation testExpectation2 = mock(TestExpectation.class);

        sut.whenCalledWithAnyOf(role1, role2)
                .then(testExpectation1)
                .whenCalledWithAnyOf(role3)
                .then(testExpectation2);

        assertThat(sut.getExpectationsByIdentifier().get(s1), is(testExpectation1));
        assertThat(sut.getExpectationsByIdentifier().get(s2), is(testExpectation1));
        assertThat(sut.getExpectationsByIdentifier().get(s3), is(testExpectation2));
    }

    @Test
    public void then_AddsTestExpectationToCurrentIdentifiers_ProducerAndConsumer() {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        UserIdentifier role3 = UserIdentifier.parse("role:ROLE_3");

        AbstractWhenThen<TestExpectation> sut = new SUT(
                setOf(role1, role2, role3),
                null,
                UserIdentifier.getAnonymous()
        );

        ConsumerProducerSet set1_1 = new ConsumerProducerSet(role1, role1);
        ConsumerProducerSet set1_2 = new ConsumerProducerSet(role1, role2);
        ConsumerProducerSet set2_2 = new ConsumerProducerSet(role2, role2);
        ConsumerProducerSet set1_3 = new ConsumerProducerSet(role1, role3);
        ConsumerProducerSet set2_3 = new ConsumerProducerSet(role2, role3);
        ConsumerProducerSet setA_3 = new ConsumerProducerSet(null, role3);

        TestExpectation testExpectation1 = mock(TestExpectation.class);
        TestExpectation testExpectation2 = mock(TestExpectation.class);

        sut.whenProducerIsAnyOf(role1)
                .whenCalledWithAnyOf(role1, role2)
                .then(testExpectation1)
                .whenProducerIsAny()
                .whenCalledWithAnyOf(role3)
                .then(testExpectation2);

        assertThat(sut.getExpectationsByIdentifier().get(set1_1), is(testExpectation1));
        assertThat(sut.getExpectationsByIdentifier().get(set1_2), is(testExpectation1));
        assertThat(sut.getExpectationsByIdentifier().get(set1_3), is(testExpectation2));

        assertThat(sut.getExpectationsByIdentifier().get(set2_2), nullValue());
        assertThat(sut.getExpectationsByIdentifier().get(set2_3), is(testExpectation2));

        assertThat(sut.getExpectationsByIdentifier().get(setA_3), is(testExpectation2));
    }

    @Test
    public void testIsCalledWithCorrectValues() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        TestExpectation expectation = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                setOf(role1, role2),
                role1, role2
        ));

        sut.whenCalledWithAnyOf(role2).then(expectation);

        sut.test();

        verify(sut).test(expectation, new ConsumerProducerSet(role1, role2));
    }

    @Test
    public void testIsCalledWithDefault() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        TestExpectation expectation = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                setOf(role1, role2),
                role1, role2
        ));
        when(sut.getDefaultExpectation(new ConsumerProducerSet(role2))).thenReturn(expectation);

        sut.test();

        verify(sut).test(expectation, new ConsumerProducerSet(role1, role2));
    }

    @Test
    public void testIsCalledWithDefaultSetWithOtherwise() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        TestExpectation userSetExpectation = mock(TestExpectation.class);
        TestExpectation expectationInSubclass = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                setOf(role1, role2),
                role1, role2
        ));
        sut.otherwise(userSetExpectation);
        when(sut.getDefaultExpectation(new ConsumerProducerSet(role1))).thenReturn(expectationInSubclass);

        sut.test();

        verify(sut).test(userSetExpectation, new ConsumerProducerSet(role1, role2));
    }

    @Test
    public void testIsCalledWithDefaultSetWithByDefault() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        TestExpectation userSetExpectation = mock(TestExpectation.class);
        TestExpectation expectationInSubclass = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                setOf(role1, role2),
                role1, role2
        ));
        sut.byDefault(userSetExpectation);
        when(sut.getDefaultExpectation(new ConsumerProducerSet(role1))).thenReturn(expectationInSubclass);

        sut.test();

        verify(sut).test(userSetExpectation, new ConsumerProducerSet(role1, role2));
    }

    @Test
    public void testDebbugerLogger() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        TestExpectation userSetExpectation = mock(TestExpectation.class);
        TestExpectation expectationInSubclass = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                setOf(role1, role2),
                role1, role2
        ));
        sut.byDefault(userSetExpectation);
        when(sut.getDefaultExpectation(new ConsumerProducerSet(role1))).thenReturn(expectationInSubclass);

        StringBuilder sb = new StringBuilder();
        sut.debugRoleMappings(sb::append);

        sut.test();

        assertThat(sb.toString(), CoreMatchers.startsWith("Running with expectations:  otherwise=Mock for TestExpectation"));
    }

}
