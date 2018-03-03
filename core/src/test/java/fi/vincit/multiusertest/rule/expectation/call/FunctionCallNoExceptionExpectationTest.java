package fi.vincit.multiusertest.rule.expectation.call;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FunctionCallNoExceptionExpectationTest {

    @Rule
    public ExpectedException expectException = ExpectedException.none();

    @Test
    public void throwIfExceptionIsExpected() throws Exception {
        FunctionCallNoExceptionExpectation sut = new FunctionCallNoExceptionExpectation();
        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExpectationNotExpected() throws Throwable {
        FunctionCallNoExceptionExpectation sut = new FunctionCallNoExceptionExpectation();

        expectException.expect(AssertionError.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

}