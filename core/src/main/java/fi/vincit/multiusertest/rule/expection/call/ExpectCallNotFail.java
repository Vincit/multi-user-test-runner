package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface ExpectCallNotFail extends Expectation {

    /**
     * Don't expect the call to fail with given users. If the call does fail with any exception,
     * then the thrown exception will pass through as is.
     * @param identifiers A set of users for which the call is not expected to fail
     * @return ExpectCall object for chaining
     */
    ExpectCallNotFail notToFail(UserIdentifiers identifiers);
}
