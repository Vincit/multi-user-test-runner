package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.util.Optional;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ExpectationInfoTest {

    @Test
    public void testValidationPass_FailModeExpectToFail() {
        new ExpectationInfo(FailMode.EXPECT_FAIL, getOptionalException(), ExpectCall.NOOP_ASSERTION);
    }

    @Test
    public void testValidationPass_FailModeExpectNotToFail() {
        new ExpectationInfo(FailMode.EXPECT_NOT_FAIL, getEmptyException(), ExpectCall.NOOP_ASSERTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeExpectNotToFail() {
        new ExpectationInfo(FailMode.EXPECT_NOT_FAIL, getOptionalException(), ExpectCall.NOOP_ASSERTION);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeNone_WithException() {
        new ExpectationInfo(FailMode.NONE, getOptionalException(), ExpectCall.NOOP_ASSERTION);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeNone_WithoutException() {
        new ExpectationInfo(FailMode.NONE, getEmptyException(), ExpectCall.NOOP_ASSERTION);
    }

    @Test
    public void testIsExceptionExpected_ExpectNotToFail() {
        ExpectationInfo expectationInfo = new ExpectationInfo(FailMode.EXPECT_NOT_FAIL, getEmptyException(), ExpectCall.NOOP_ASSERTION);
        assertThat(expectationInfo.isExceptionExpected(new Throwable(), Throwable.class), is(false));
    }

    @Test
    public void testIsExceptionExpected_ExpectToFail_DefaultException() {
        ExpectationInfo expectationInfo = new ExpectationInfo(FailMode.EXPECT_FAIL, getEmptyException(), ExpectCall.NOOP_ASSERTION);
        assertThat(expectationInfo.isExceptionExpected(new Throwable(), Throwable.class), is(true));
    }

    @Test
    public void testIsExceptionExpected_ExpectToFail_GivenException() {
        ExpectationInfo expectationInfo = new ExpectationInfo(FailMode.EXPECT_FAIL, getOptionalException(), ExpectCall.NOOP_ASSERTION);
        assertThat(expectationInfo.isExceptionExpected(new Throwable(), RuntimeException.class), is(true));
    }

    @Test
    public void testAssertExceptionCall() throws Throwable {
        ExceptionAssertionCall call = mock(ExceptionAssertionCall.class);
        ExpectationInfo expectationInfo = new ExpectationInfo(FailMode.EXPECT_FAIL, getOptionalException(), call);
        Throwable exception = new Throwable();

        expectationInfo.assertException(exception);

        verify(call).assertException(eq(exception));
    }

    private Optional<Class<? extends Throwable>> getOptionalException() {
        return Optional.<Class<? extends Throwable>>of(Throwable.class);
    }

    private Optional<Class<? extends Throwable>> getEmptyException() {
        return Optional.empty();
    }
}