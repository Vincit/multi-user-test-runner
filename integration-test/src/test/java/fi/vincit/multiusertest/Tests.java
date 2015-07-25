package fi.vincit.multiusertest;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import org.junit.Test;

public class Tests {
    @Test
    public void smoke() throws Throwable {
        AuthorizationRule rule = new AuthorizationRule();
    }
}
