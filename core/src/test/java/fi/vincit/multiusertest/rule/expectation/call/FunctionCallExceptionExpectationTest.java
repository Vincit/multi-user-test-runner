package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.rule.expectation.AssertionCalled;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FunctionCallExceptionExpectationTest {
    @Rule
    public ExpectedException expectException = ExpectedException.none();

    @Test
    public void throwIfExceptionIsExpected() throws Exception {
        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class
                );

        expectException.expect(AssertionError.class);
        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExpectationNotExpected() throws Throwable {
        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException());
    }

    @Test
    public void throwIfExpectationNotExpected_UnexpectedException() throws Throwable {
        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class
                );

        expectException.expect(AssertionError.class);
        expectException.expectMessage("Unexpected exception thrown with role <anonymous>: Expected <IllegalStateException> but was <IllegalArgumentException>");
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void throwIfExpectationNotExpected_CaptureDerivedExceptions() throws Throwable {
        FunctionCallExceptionExpectation<RuntimeException> sut =
                new FunctionCallExceptionExpectation<>(
                        RuntimeException.class
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void throwIfExpectationNotExpected_Assert() throws Throwable {
        AssertionCalled called = new AssertionCalled();

        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class,
                        expectException -> called.withThrowable(expectException)
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException("Foo"));
        assertThat(called.getException().getMessage(), is("Foo"));
    }

    @Test
    public void throwIfExpectationNotExpected_DontAssertIfWrongException() throws Throwable {
        AssertionCalled called = new AssertionCalled();

        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class,
                        expectException -> called.withThrowable(expectException)
                );

        expectException.expect(AssertionError.class);
        expectException.expectMessage("Unexpected exception thrown with role <anonymous>: Expected <IllegalStateException> but was <IllegalArgumentException>: Foo");
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException("Foo"));
        assertThat(called.getException(), nullValue());
    }

}