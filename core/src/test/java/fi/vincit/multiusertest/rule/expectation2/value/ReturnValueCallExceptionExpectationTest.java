package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.rule.expectation2.AssertionCalled;
import fi.vincit.multiusertest.rule.expectation2.CallbackCalled;
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
        AssertionCalled called = new AssertionCalled();

        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(
                        IllegalStateException.class,
                        expectException -> called.withThrowable(expectException)
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException("Foo"));

        assertThat(called.getException().getMessage(), is("Foo"));
    }

    @Test
    public void throwIfExpectationNotExpected_WrongType() throws Throwable {
        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(IllegalStateException.class);

        expectException.expect(AssertionError.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void callAndAssertValue() throws Throwable {
        CallbackCalled called = new CallbackCalled();
        ReturnValueCallExceptionExpectation<Object, IllegalStateException> sut =
                new ReturnValueCallExceptionExpectation<>(IllegalStateException.class);

        sut.callAndAssertValue(called::markCalled);

        assertThat(called.wasCalled(), is(true));
    }

}