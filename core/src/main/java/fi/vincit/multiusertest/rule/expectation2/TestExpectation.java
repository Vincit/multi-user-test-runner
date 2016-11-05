package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.util.UserIdentifier;

public interface TestExpectation {

    void handleExceptionNotThrown(UserIdentifier userIdentifier);
    void handleThrownException(UserIdentifier userIdentifier, Throwable e) throws Throwable;

}
