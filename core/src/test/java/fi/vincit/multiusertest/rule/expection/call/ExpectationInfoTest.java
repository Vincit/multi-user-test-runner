package fi.vincit.multiusertest.rule.expection.call;

import org.junit.Test;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.util.Optional;

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

    private Optional<Class<? extends Throwable>> getOptionalException() {
        return Optional.<Class<? extends Throwable>>of(Throwable.class);
    }

    private Optional<Class<? extends Throwable>> getEmptyException() {
        return Optional.empty();
    }
}