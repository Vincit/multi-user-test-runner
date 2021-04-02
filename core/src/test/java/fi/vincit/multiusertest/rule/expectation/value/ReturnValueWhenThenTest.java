package fi.vincit.multiusertest.rule.expectation.value;


import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.ReturnValueCall;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ReturnValueWhenThenTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void getDefaultExpectation() {
        ReturnValueWhenThen<Integer> sut = new ReturnValueWhenThen<>(
                () -> 1,
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class)
        );

        ReturnValueCallNoExceptionExpectation<Integer> defaultExpectation =
                (ReturnValueCallNoExceptionExpectation)sut.getDefaultExpectation(new ConsumerProducerSet(UserIdentifier.getAnonymous()));

        assertThat(defaultExpectation, isA(ReturnValueCallNoExceptionExpectation.class));
    }

    @Test
    public void test_AssertsValue() throws Throwable {
        ReturnValueCall<Integer> call = () -> 1;
        ReturnValueWhenThen<Integer> sut = new ReturnValueWhenThen<>(
                call,
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class)
        );

        TestValueExpectation expectation = mock(TestValueExpectation.class);
        sut.test(expectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));

        verify(expectation).callAndAssertValue(call);
    }

    @Test
    public void test_CallThrows_WhenNotExpected() throws Throwable {
        IllegalArgumentException originalException = new IllegalArgumentException("Thrown from call");
        ReturnValueCall<Integer> call = () -> 1;
        ReturnValueWhenThen<Integer> sut = new ReturnValueWhenThen<>(
                call,
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class)
        );

        TestValueExpectation expectation = mock(TestValueExpectation.class);
        doThrow(originalException)
                .when(expectation)
                .callAndAssertValue(any());
        doThrow(new IllegalStateException("Thrown from expectation"))
                .when(expectation)
                .handleThrownException(new ConsumerProducerSet(UserIdentifier.getAnonymous()), originalException);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Thrown from expectation");
        sut.test(expectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void test_CallThrows_WhenExpectedButNotThrown() throws Throwable {
        ReturnValueWhenThen<Integer> sut = new ReturnValueWhenThen<>(
                () -> 1,
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class)
        );

        TestValueExpectation expectation = mock(TestValueExpectation.class);
        doThrow(new IllegalStateException("Thrown from expectation"))
                .when(expectation)
                .handleExceptionNotThrown(new ConsumerProducerSet(UserIdentifier.getAnonymous()));

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Thrown from expectation");
        sut.test(expectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void test_CallDoesntThrow_WhenExpectedAndThrown() throws Throwable {
        ReturnValueWhenThen<Integer> sut = new ReturnValueWhenThen<>(
                () -> 1,
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class)
        );

        TestValueExpectation expectation = mock(TestValueExpectation.class);
        doThrow(new IllegalStateException(""))
                        .when(expectation)
                        .callAndAssertValue(any());

        sut.test(expectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void test_CallDoesntThrown_WhenNotExpectedAndNotThrown() throws Throwable {
        ReturnValueWhenThen<Integer> sut = new ReturnValueWhenThen<>(
                () -> 1,
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class)
        );

        TestValueExpectation expectation = mock(TestValueExpectation.class);

        sut.test(expectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }
}
