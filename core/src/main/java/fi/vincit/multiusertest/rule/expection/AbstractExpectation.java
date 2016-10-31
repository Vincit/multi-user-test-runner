package fi.vincit.multiusertest.rule.expection;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.call.ExpectationInfo;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractExpectation<T extends ExpectationInfo> implements Expectation {


    private Class<? extends Throwable> defaultExpectedException;

    private FailMode generalFailMode = FailMode.NONE;

    private final Map<UserIdentifier, T> expectations = new HashMap<>();

    @Override
    public void setExpectedException(Class<? extends Throwable> expectedException) {
        this.defaultExpectedException = expectedException;
    }

    protected void throwIfExceptionIsExpected(UserIdentifier userIdentifier) {
        Optional<? extends ExpectationInfo> possibleCallInfo = getFailInfo(userIdentifier);
        if (possibleCallInfo.isPresent()) {
            ExpectationInfo valueOfInfo = possibleCallInfo.get();
            Class<? extends Throwable> exception = valueOfInfo
                    .getExceptionClass()
                    .orElse(defaultExpectedException);

            if (valueOfInfo.getFailMode() == FailMode.EXPECT_FAIL) {
                throw new AssertionError("Expected to fail with exception " + exception.getName());
            }
        }
    }

    protected void throwIfExpectationNotExpected(UserIdentifier userIdentifier, Throwable e)  throws Throwable {
        Optional<? extends ExpectationInfo> possibleCallInfo = getFailInfo(userIdentifier);
        if (possibleCallInfo.isPresent()) {
            ExpectationInfo expectationInfo = possibleCallInfo.get();

            if (expectationInfo.getFailMode() == FailMode.EXPECT_NOT_FAIL) {
                throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
            } else {
                if (!expectationInfo.isExceptionExpected(e, defaultExpectedException)) {
                    throw e;
                }
                expectationInfo.assertException(e);
            }
        } else {
            if (generalFailMode != FailMode.EXPECT_NOT_FAIL) {
                throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
            }
        }
    }


    protected Map<UserIdentifier, T> getExpectations() {
        return expectations;
    }

    protected void setGeneralFailMode(FailMode generalFailMode) {
        this.generalFailMode = generalFailMode;
    }


    private Optional<? extends ExpectationInfo> getFailInfo(UserIdentifier userIdentifier) {
        if (getExpectations().containsKey(userIdentifier)) {
            return Optional.of(getExpectations().get(userIdentifier));
        } else {
            return Optional.empty();
        }
    }
}
