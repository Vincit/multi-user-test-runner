package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface ExpectCallFail extends Expectation {
    ExpectCallFail toFail(UserIdentifiers identifiers);

    ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers);
}
