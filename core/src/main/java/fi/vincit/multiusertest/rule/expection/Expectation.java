package fi.vincit.multiusertest.rule.expection;

import fi.vincit.multiusertest.util.UserIdentifier;

public interface Expectation {

    void execute(UserIdentifier userIdentifier) throws Throwable;

}
