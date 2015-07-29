package fi.vincit.multiusertest.rule.expection.call;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

/**
 * Expect a function call to throw or not throw an exception. Default exception expected is
 * {@link AccessDeniedException}. A custom exception can be given via {@link ExpectCall#toFailWithException(Class, UserIdentifiers)}
 * method.
 */
public class ExpectCall implements Expectation {

    private final FunctionCall functionCall;

    private Map<UserIdentifier, CallInfo> expectations = new HashMap<>();

    public ExpectCall(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    /**
     * Expect the call to fail with given users. If the call doesn't fail with AccessDenied exception,
     * then AssertionError will be thrown.
     * @param identifiers A set of user identifiers for which the call is expected to fail
     * @return ExpectCall object for chaining
     */
    public ExpectCall toFail(UserIdentifiers identifiers) {
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new CallInfo(FailMode.EXPECT_FAIL, Optional.<Class<? extends Throwable>>of(AccessDeniedException.class)));
        }
        return this;
    }

    /**
     * Expect the call to fail with a defined exception for given users. If the call doesn't fail with
     * the specified exception, AssertionError will be thrown.
     * @param exception Exception to expect
     * @param identifiers A set of user identifiers for which the call is expected to fail
     * @return ExpectCall object for chaining
     */
    public ExpectCall toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception must be given");
        }
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new CallInfo(FailMode.EXPECT_FAIL, Optional.<Class<? extends Throwable>>of(exception)));
        }
        return this;
    }

    /**
     * Don't expect the call to fail with given users. If the call does fail with any exception,
     * then AssertionError will be thrown.
     * @param identifiers A set of users for which the call is not expected to fail
     * @return ExpectCall object for chaining
     */
    public ExpectCall notToFail(UserIdentifiers identifiers) {
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new CallInfo(FailMode.EXPECT_NOT_FAIL, Optional.<Class<? extends Throwable>>empty()));
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier userIdentifier) throws Throwable {
        try {
            functionCall.call();
        } catch (Throwable e) {
            throwIfExpectionNotExpected(userIdentifier, e);
            return;
        }
        throwIfExceptionIsExpected(userIdentifier);
    }

    private void throwIfExceptionIsExpected(UserIdentifier userIdentifier) {
        Optional<CallInfo> possibleCallInfo = getFailInfo(userIdentifier);
        if (possibleCallInfo.isPresent()) {
            CallInfo callInfo = possibleCallInfo.get();

            if (callInfo.getFailMode() == FailMode.EXPECT_FAIL) {
                throw new AssertionError("Expected to fail with exception " + callInfo.getExceptionClass().get().getName());
            }
        }
    }

    private void throwIfExpectionNotExpected(UserIdentifier userIdentifier, Throwable e) {
        Optional<CallInfo> possibleCallInfo = getFailInfo(userIdentifier);
        if (possibleCallInfo.isPresent()) {
            CallInfo callInfo = possibleCallInfo.get();;

            if (callInfo.getFailMode() == FailMode.EXPECT_NOT_FAIL) {
                throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
            } else {
                if (!callInfo.isExceptionExpected(e)) {
                    throw new AssertionError("Expected to fail with user role, but with different type of exception. " +
                            "Expected <" + callInfo.getExceptionClass().get() + "> but was <" + e.getClass().getName() + ">", e);
                }
            }
        } else {
            throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
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
