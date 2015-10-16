package fi.vincit.multiusertest.rule.expection.call;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

/**
 * <p>
 * Expect a function call to throw or not throw an exception. Default exception is
 * {@link fi.vincit.multiusertest.rule.AuthorizationRule}'s exception.
 * A custom exception can be given via {@link ExpectCall#toFailWithException(Class, UserIdentifiers)}
 * method.
 * </p>
 * <p>
 * toFail* and notToFail methods can't be mixed together. I.e. only <code>toFail(...).toFailWithException(...)</code> and
 * <code>notToFail(...).notToFail(...)</code> calls are allowed. <b>Not</b> <code>toFail(...).notToFail(...)</code>.
 * Otherwise it would be difficult to determine what to do with the definitions that are not defined since
 * the expectation doesn't have access to all available definitions.
 * </p>
 * <p>
 * Use {@link fi.vincit.multiusertest.rule.expection.Expectations} to create instances.
 * </p>
 */
public class ExpectCall implements ExpectCallFail, ExpectCallNotFail {

    public static final ExceptionAssertionCall NOOP_ASSERTION = new ExceptionAssertionCall() {
        @Override
        public void assertException(Throwable thrownException) {
        }
    };

    private Class<? extends Throwable> defaultExpectedException;

    private final FunctionCall functionCall;

    private final Map<UserIdentifier, CallInfo> expectations = new HashMap<>();

    private FailMode generalFailMode = FailMode.NONE;

    public ExpectCall(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    @Override
    public ExpectCallFail toFail(UserIdentifiers identifiers) {
        Objects.requireNonNull(identifiers, "Identifiers must not be null");
        generalFailMode = FailMode.EXPECT_FAIL;
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new CallInfo(
                    FailMode.EXPECT_FAIL,
                    Optional.<Class<? extends Throwable>>empty(),
                    NOOP_ASSERTION
            ));
        }
        return this;
    }

    @Override
    public ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers) {
        return toFailWithException(exception, identifiers, NOOP_ASSERTION);
    }

    @Override
    public ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers, ExceptionAssertionCall exceptionAssertionCall) {
        Objects.requireNonNull(exception, "Exception must not be null");
        Objects.requireNonNull(exceptionAssertionCall, "ExceptionAssertionCall must not be null");
        Objects.requireNonNull(identifiers, "Identifiers must not be null");

        generalFailMode = FailMode.EXPECT_FAIL;
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new CallInfo(
                    FailMode.EXPECT_FAIL,
                    Optional.<Class<? extends Throwable>>of(exception),
                    exceptionAssertionCall
            ));
        }
        return this;
    }

    @Override
    public ExpectCallNotFail notToFail(UserIdentifiers identifiers) {
        Objects.requireNonNull(identifiers, "Identifiers must not be null");
        generalFailMode = FailMode.EXPECT_NOT_FAIL;
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new CallInfo(FailMode.EXPECT_NOT_FAIL, Optional.<Class<? extends Throwable>>empty(), NOOP_ASSERTION));
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier userIdentifier) throws Throwable {
        try {
            functionCall.call();
        } catch (Throwable e) {
            throwIfExpectationNotExpected(userIdentifier, e);
            return;
        }
        throwIfExceptionIsExpected(userIdentifier);
    }

    @Override
    public void setExpectedException(Class<? extends Throwable> expectedException) {
        defaultExpectedException = expectedException;
    }

    private void throwIfExceptionIsExpected(UserIdentifier userIdentifier) {
        Optional<CallInfo> possibleCallInfo = getFailInfo(userIdentifier);
        if (possibleCallInfo.isPresent()) {
            CallInfo callInfo = possibleCallInfo.get();
            Class<? extends Throwable> exception =
                    callInfo.getExceptionClass().orElse(defaultExpectedException);

            if (callInfo.getFailMode() == FailMode.EXPECT_FAIL) {
                throw new AssertionError("Expected to fail with exception " + exception.getName());
            }
        }
    }

    private void throwIfExpectationNotExpected(UserIdentifier userIdentifier, Throwable e)  throws Throwable {
        Optional<CallInfo> possibleCallInfo = getFailInfo(userIdentifier);
        if (possibleCallInfo.isPresent()) {
            CallInfo callInfo = possibleCallInfo.get();;

            if (callInfo.getFailMode() == FailMode.EXPECT_NOT_FAIL) {
                throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
            } else {
                if (!callInfo.isExceptionExpected(e, defaultExpectedException)) {
                    throw e;
                }
                callInfo.assertException(e);
            }
        } else {
            if (generalFailMode != FailMode.EXPECT_NOT_FAIL) {
                throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
            }
        }
    }

    private Optional<CallInfo> getFailInfo(UserIdentifier userIdentifier) {
        if (expectations.containsKey(userIdentifier)) {
            return Optional.of(expectations.get(userIdentifier));
        } else {
            return Optional.empty();
        }
    }
}
