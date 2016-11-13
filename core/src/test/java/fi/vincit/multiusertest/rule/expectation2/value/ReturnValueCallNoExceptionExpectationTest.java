package fi.vincit.multiusertest.rule.expectation2.value;

import fi.vincit.multiusertest.rule.expectation2.CallbackCalled;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReturnValueCallNoExceptionExpectationTest {
    @Rule
    public ExpectedException expectException = ExpectedException.none();

    @Test
    public void throwIfExceptionIsExpected() throws Exception {
        ReturnValueCallNoExceptionExpectation<Integer> sut =
                new ReturnValueCallNoExceptionExpectation<>();

        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExpectationNotExpected() throws Throwable {
        ReturnValueCallNoExceptionExpectation<Integer> sut =
                new ReturnValueCallNoExceptionExpectation<>();

        expectException.expect(AssertionError.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void callAndAssertValue() throws Throwable {
        CallbackCalled called = new CallbackCalled();
        ReturnValueCallNoExceptionExpectation<Object> sut =
                new ReturnValueCallNoExceptionExpectation<>();
        sut.callAndAssertValue(called::markCalled);

        assertThat(called.wasCalled(), is(true));
    }

    @Test
    public void callAndAssertValue_throws() throws Throwable {
        ReturnValueCallNoExceptionExpectation<Integer> sut =
                new ReturnValueCallNoExceptionExpectation<>();

        expectException.expect(IllegalArgumentException.class);
        sut.callAndAssertValue(() -> {throw new IllegalArgumentException();});
    }

}