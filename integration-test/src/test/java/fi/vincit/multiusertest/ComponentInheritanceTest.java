package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.TestBaseClass;
import org.junit.Test;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;
import static fi.vincit.multiusertest.util.UserIdentifiers.users;

@RunWithUsers(producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_ADMIN")
public class ComponentInheritanceTest extends TestBaseClass {

    public void pass() {
    }

    public void fail() {
        throw new IllegalStateException();
    }

    @Test
    public void passes() throws Throwable {
        authorizationRule.testCall(this::pass)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(expectNotToFail())
                .test();
    }

    @Test
    public void passes_users_roles_syntax() throws Throwable {
        authorizationRule.testCall(this::pass)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"), users("foo"))
                .then(expectNotToFail())
                .test();
    }

    @Test
    public void fails() throws Throwable {
        authorizationRule.testCall(this::fail)
                .whenCalledWithAnyOf(roles("ROLE_ADMIN"))
                .then(expectException(IllegalStateException.class))
                .test();
    }
}
