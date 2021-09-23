package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.expectation.ConsumerProducerSet;
import fi.vincit.multiusertest.rule.expectation.TestExpectation;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class FunctionCallWhenThenTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_CallDoesntThrow_WhenNotExpectedAndNotThrown() throws Throwable {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {},
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class),
                new HashSet<>()
        );

        sut.test(mock(TestExpectation.class), new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void test_CallDoesntThrow_WhenExpectedAndThrown() throws Throwable {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {throw new IllegalArgumentException();},
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class),
                new HashSet<>()
        );

        sut.test(mock(TestExpectation.class), new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void test_CallThrows_WhenExpectedButNotThrown() throws Throwable {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {},
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class),
                new HashSet<>()
        );

        TestExpectation testExpectation = mock(TestExpectation.class);
        doThrow(new IllegalStateException("Thrown from expectation"))
                .when(testExpectation)
                .handleExceptionNotThrown(new ConsumerProducerSet(UserIdentifier.getAnonymous()));

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Thrown from expectation");
        sut.test(testExpectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void test_CallThrows_WhenNotExpectedButThrown() throws Throwable {
        RuntimeException originalThrownException = new RuntimeException("Thrown from call");
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {throw originalThrownException;},
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class),
                new HashSet<>()
        );

        TestExpectation testExpectation = mock(TestExpectation.class);
        doThrow(new IllegalStateException("Thrown from expectation"))
                .when(testExpectation)
                .handleThrownException(new ConsumerProducerSet(UserIdentifier.getAnonymous()), originalThrownException);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Thrown from expectation");
        sut.test(testExpectation, new ConsumerProducerSet(UserIdentifier.getAnonymous()));
    }

    @Test
    public void getDefaultExpectation() {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {},
                null,
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class),
                mock(UserRoleIT.class),
                new HashSet<>()
        );

        assertThat(
                (FunctionCallNoExceptionExpectation) sut.getDefaultExpectation(new ConsumerProducerSet(UserIdentifier.getAnonymous())),
                isA(FunctionCallNoExceptionExpectation.class)
        );
    }

}
