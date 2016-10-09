package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface ExpectCallFail extends Expectation {
    /**
     * Expect the call to fail with given users. If the call doesn't fail with AccessDenied exception,
     * then the thrown exception will pass through as is.
     * @param identifiers A set of user identifiers for which the call is expected to fail
     * @return ExpectCall object for chaining
     */
    ExpectCallFail toFail(UserIdentifiers identifiers);

    /**
     * Expect the call to fail with a defined exception for given users. If the call doesn't fail with
     * the specified exception then the thrown exception will pass through as is.
     * @param exception Exception to expect
     * @param identifiers A set of user identifiers for which the call is expected to fail
     * @return ExpectCall object for chaining
     */
    ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers);

    /**
     * Expect the call to fail with a defined exception for given users. The expected exception values can
     * be verified in the given exceptionAssertionCall. If the call doesn't fail with the specified exception
     * then the thrown exception will pass through as is.
     * @param exception Exception to expect
     * @param identifiers A set of user identifiers for which the call is expected to fail
     * @param exceptionAssertionCall Anonymous class or lambda expression where thrown expected exception values can be verified
     * @return ExpectCall object for chaining
     * @since 0.4
     */
    ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers, ExceptionAssertionCall exceptionAssertionCall);

    void execute();
}
