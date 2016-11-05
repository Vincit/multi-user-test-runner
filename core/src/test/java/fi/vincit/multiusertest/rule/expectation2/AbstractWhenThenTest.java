package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class AbstractWhenThenTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static <T> Set<T> setOf(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static class SUT extends AbstractWhenThen<TestExpectation> {

        public SUT(UserIdentifier userIdentifier) {
            super(userIdentifier, mock(AuthorizationRule.class));
        }

        @Override
        protected void test(TestExpectation testExpectation, UserIdentifier userIdentifier) throws Throwable {
        }

        @Override
        protected TestExpectation getDefaultExpectation(UserIdentifier userIdentifier) {
            return null;
        }
    }

    @Test
    public void whenCalledWith_UserIdentifier() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWith(UserIdentifier.getAnonymous(), UserIdentifier.getProducer());

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.getAnonymous(), UserIdentifier.getProducer()))
        );
    }

    @Test
    public void whenCalledWith_UserIdentifier_ThrowIfSameAddedTwice() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.whenCalledWith(UserIdentifier.getAnonymous(), UserIdentifier.getAnonymous());
    }

    @Test
    public void whenCalledWith_UserIdentifiers() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        sut.whenCalledWith(UserIdentifiers.anyOf("role:ROLE_ADMIN", "role:ROLE_USER"));

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_ADMIN"), UserIdentifier.parse("role:ROLE_USER")))
        );
    }

    @Test
    public void whenCalledWith_UserIdentifiers_ThrowsIfNoIdentifiersGiven() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalArgumentException.class);
        sut.whenCalledWith(new UserIdentifiers[] {});

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf())
        );
    }

    @Test
    public void whenCalledWith_UserIdentifier_ThrowsIfNoIdentifiersGiven() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalArgumentException.class);
        sut.whenCalledWith(new UserIdentifier[] {});

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf())
        );
    }

    @Test
    public void whenCalledWith_UserIdentifiers_CurrentCleared_OnEachCall() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );
        sut.getCurrentIdentifiers().add(UserIdentifier.getAnonymous());

        sut.whenCalledWith(UserIdentifiers.anyOf("role:ROLE_USER"));

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.parse("role:ROLE_USER")))
        );
    }

    @Test
    public void whenCalledWith_UserIdentifier_CurrentCleared_OnEachCall() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );
        sut.getCurrentIdentifiers().add(UserIdentifier.getAnonymous());

        sut.whenCalledWith(UserIdentifier.getProducer());

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf(UserIdentifier.getProducer()))
        );
    }


    @Test
    public void whenCalledWith_UserIdentifiers_CurrentCleared_WhenThrown() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );
        sut.getCurrentIdentifiers().add(UserIdentifier.getAnonymous());

        expectedException.expect(IllegalArgumentException.class);
        sut.whenCalledWith(new UserIdentifiers[] {});

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf())
        );
    }

    @Test
    public void whenCalledWith_UserIdentifier_CurrentCleared_WhenThrown() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );
        sut.getCurrentIdentifiers().add(UserIdentifier.getAnonymous());

        expectedException.expect(IllegalArgumentException.class);
        sut.whenCalledWith(new UserIdentifier[] {});

        assertThat(
                sut.getCurrentIdentifiers(),
                is(setOf())
        );
    }

    @Test
    public void then_CalledBeforeWhen_Throws() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.then(mock(TestExpectation.class));
    }

    @Test
    public void then_TryingToOverrideSetExpectation_Throws() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.whenCalledWith(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class))
            .whenCalledWith(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class));
    }

    @Test
    public void then_TryingToOverrideSetExpectation_CurrentIdentifiersIsCleared() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );

        expectedException.expect(IllegalStateException.class);
        sut.whenCalledWith(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class))
            .whenCalledWith(UserIdentifier.getAnonymous())
            .then(mock(TestExpectation.class));
        assertThat(sut.getCurrentIdentifiers(), is(setOf()));
    }

    @Test
    public void then_AddsTestExpectationToCurrentIdentifiers() {
        AbstractWhenThen<TestExpectation> sut = new SUT(
                UserIdentifier.getAnonymous()
        );
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        UserIdentifier role2 = UserIdentifier.parse("role:ROLE_2");
        UserIdentifier role3 = UserIdentifier.parse("role:ROLE_3");

        TestExpectation testExpectation1 = mock(TestExpectation.class);
        TestExpectation testExpectation2 = mock(TestExpectation.class);

        sut.whenCalledWith(role1, role2)
                .then(testExpectation1)
                .whenCalledWith(role3)
                .then(testExpectation2);

        assertThat(sut.getExpectationsByIdentifier().get(role1), is(testExpectation1));
        assertThat(sut.getExpectationsByIdentifier().get(role2), is(testExpectation1));
        assertThat(sut.getExpectationsByIdentifier().get(role3), is(testExpectation2));
    }

    @Test
    public void testIsCalledWithCorrectValues() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        TestExpectation expectation = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                role1
        ));

        sut.whenCalledWith(role1).then(expectation);

        sut.test();

        verify(sut).test(expectation, role1);
    }

    @Test
    public void testIsCalledWithDefault() throws Throwable {
        UserIdentifier role1 = UserIdentifier.parse("role:ROLE_1");
        TestExpectation expectation = mock(TestExpectation.class);

        AbstractWhenThen<TestExpectation> sut = spy(new SUT(
                role1
        ));
        when(sut.getDefaultExpectation(role1)).thenReturn(expectation);

        sut.test();

        verify(sut).test(expectation, role1);
    }

}