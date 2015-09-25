package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface ExpectCallNotFail extends Expectation {
    ExpectCallNotFail notToFail(UserIdentifiers identifiers);
}
