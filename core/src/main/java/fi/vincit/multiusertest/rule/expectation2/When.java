package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

public interface When<EXPECTATION extends TestExpectation> {

    WhenThen<EXPECTATION> whenCalledWith(UserIdentifiers... userIdentifiers);

    WhenThen<EXPECTATION> whenCalledWith(UserIdentifier... userIdentifiers);
}
