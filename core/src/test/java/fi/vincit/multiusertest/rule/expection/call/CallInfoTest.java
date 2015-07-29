package fi.vincit.multiusertest.rule.expection.call;

import org.junit.Test;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.util.Optional;

public class CallInfoTest {

    @Test
    public void testValidationPass_FailModeExpectToFail() {
        new CallInfo(FailMode.EXPECT_FAIL, getOptionalException());
    }

    @Test
    public void testValidationPass_FailModeExpectNotToFail() {
        new CallInfo(FailMode.EXPECT_NOT_FAIL, getEmptyException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeExpectToFail() {
        new CallInfo(FailMode.EXPECT_FAIL, getEmptyException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeExpectNotToFail() {
        new CallInfo(FailMode.EXPECT_NOT_FAIL, getOptionalException());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeNone_WithException() {
        new CallInfo(FailMode.NONE, getOptionalException());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidationFail_FailModeNone_WithoutException() {
        new CallInfo(FailMode.NONE, getEmptyException());
    }

    private Optional<Class<? extends Throwable>> getOptionalException() {
        return Optional.<Class<? extends Throwable>>of(Throwable.class);
    }

    private Optional<Class<? extends Throwable>> getEmptyException() {
        return Optional.empty();
    }
}