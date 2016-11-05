package fi.vincit.multiusertest.rule.expectation2.value;

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
        Called called = new Called();
        ReturnValueCallNoExceptionExpectation<Integer> sut =
                new ReturnValueCallNoExceptionExpectation<>();
        sut.callAndAssertValue(() -> {called.called = true; return 1;});

        assertThat(called.called, is(true));
    }

    @Test
    public void callAndAssertValue_throws() throws Throwable {
        ReturnValueCallNoExceptionExpectation<Integer> sut =
                new ReturnValueCallNoExceptionExpectation<>();

        expectException.expect(IllegalArgumentException.class);
        sut.callAndAssertValue(() -> {throw new IllegalArgumentException();});
    }

    private static class Called {
        boolean called;
    }
}