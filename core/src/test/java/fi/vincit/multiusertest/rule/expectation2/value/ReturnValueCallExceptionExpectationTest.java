package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReturnValueCallExceptionExpectationTest {

    @Rule
    public ExpectedException expectException = ExpectedException.none();

    @Test
    public void throwIfExceptionIsExpected() throws Throwable {
        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(IllegalStateException.class);

        expectException.expect(AssertionError.class);
        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExpectationNotExpected() throws Throwable {
        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(IllegalStateException.class);

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException());
    }

    @Test
    public void throwIfExpectationNotExpected_CustomAssert() throws Throwable {
        AssertCalled isCalled = new AssertCalled();

        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(
                        IllegalStateException.class,
                        expectException -> isCalled.calledWith = expectException
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException("Foo"));

        assertThat(isCalled.calledWith.getMessage(), is("Foo"));
    }

    @Test
    public void throwIfExpectationNotExpected_WrongType() throws Throwable {
        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(IllegalStateException.class);

        expectException.expect(IllegalArgumentException.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void callAndAssertValue() throws Throwable {
        CallbackCalled called = new CallbackCalled();
        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(IllegalStateException.class);

        sut.callAndAssertValue(() -> called.called = true);

        assertThat(called.called, is(true));
    }

    private static class CallbackCalled {
        private boolean called;
    }

    private static class AssertCalled {
        private Throwable calledWith;
    }

}