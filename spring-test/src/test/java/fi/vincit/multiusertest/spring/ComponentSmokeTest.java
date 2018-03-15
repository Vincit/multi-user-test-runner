package fi.vincit.multiusertest.spring;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.context.ComponentTestContext;
import fi.vincit.multiusertest.context.TestConfiguration;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.User;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.IOException;

import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectException;
import static fi.vincit.multiusertest.rule.expectation.TestExpectations.expectNotToFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.roles;

@RunWithUsers(
        producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_USER"
)
@MultiUserTestConfig
@RunWith(MultiUserTestRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class, ComponentTestContext.class})
public class ComponentSmokeTest {

    @Autowired
    @MultiUserConfigClass
    private MultiUserConfig<User, User.Role> multiUserConfig;

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Before
    public void init() {
        this.multiUserConfig.setAuthorizationRule(authorizationRule);
    }

    private void pass() {
    }

    private void fail() {
        throw new IllegalStateException();
    }

    private void failIO() throws IOException {
        throw new IOException();
    }

    @Test
    public void testNotFail() throws Throwable {
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::pass)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectNotToFail())
                .test();
    }

    @Test
    public void testFail() throws Throwable {
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::fail)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test
    public void testFail_CustomException() throws Throwable {
        multiUserConfig.logInAs(LoginRole.CONSUMER);

        authorizationRule.testCall(this::failIO)
                .whenCalledWithAnyOf(roles("ROLE_USER"))
                .then(expectException(IOException.class))
                .test();
    }
}
