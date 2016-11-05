package fi.vincit.multiusertest.rule.expectation2.call;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.rule.expectation2.TestExpectation;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
                () -> {}, UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class)
        );

        sut.test(mock(TestExpectation.class), UserIdentifier.getAnonymous());
    }

    @Test
    public void test_CallDoesntThrow_WhenExpectedAndThrown() throws Throwable {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {throw new IllegalArgumentException();}, UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class)
        );

        sut.test(mock(TestExpectation.class), UserIdentifier.getAnonymous());
    }

    @Test
    public void test_CallThrows_WhenExpectedButNotThrown() throws Throwable {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {}, UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class)
        );

        TestExpectation testExpectation = mock(TestExpectation.class);
        doThrow(new IllegalStateException("Thrown from expectation"))
                .when(testExpectation)
                .handleExceptionNotThrown(UserIdentifier.getAnonymous());

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Thrown from expectation");
        sut.test(testExpectation, UserIdentifier.getAnonymous());
    }

    @Test
    public void test_CallThrows_WhenNotExpectedButThrown() throws Throwable {
        RuntimeException originalThrownException = new RuntimeException("Thrown from call");
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {throw originalThrownException;},
                UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class)
        );

        TestExpectation testExpectation = mock(TestExpectation.class);
        doThrow(new IllegalStateException("Thrown from expectation"))
                .when(testExpectation)
                .handleThrownException(UserIdentifier.getAnonymous(), originalThrownException);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Thrown from expectation");
        sut.test(testExpectation, UserIdentifier.getAnonymous());
    }

    @Test
    public void getDefaultExpectation() {
        FunctionCallWhenThen sut = new FunctionCallWhenThen(
                () -> {}, UserIdentifier.getAnonymous(),
                mock(AuthorizationRule.class)
        );

        assertThat(
                (FunctionCallNoExceptionExpectation) sut.getDefaultExpectation(UserIdentifier.getAnonymous()),
                isA(FunctionCallNoExceptionExpectation.class)
        );
    }

}