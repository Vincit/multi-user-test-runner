package fi.vincit.multiusertest.spring;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.context.TestConfiguration;
import fi.vincit.multiusertest.context.TestContext;
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
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static fi.vincit.multiusertest.rule.Authentication.notToFail;
import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

@RunWithUsers(
        producers = {"role:ROLE_ADMIN"}, consumers = "role:ROLE_USER"
)
@MultiUserTestConfig(
        defaultException = AccessDeniedException.class
)
@RunWith(MultiUserTestRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class, TestContext.class})
public class ComponentSmokeTest {

    @Parameterized.Parameters
    public static Collection<Object[]> foo() {
        return Arrays.asList(new Object[][] {
                {}, {}
        });
    }

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
        this.multiUserConfig.setAuthorizationRule(authorizationRule, this);
    }

    @Test
    public void testNotFail() {
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(notToFail(ifAnyOf("role:ROLE_USER")));
    }

    @Test
    public void testFail() {
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:ROLE_USER")));
        throw new AccessDeniedException("Denied");
    }

    @Test
    public void testFail_CustomException() throws IOException {
        authorizationRule.setExpectedException(IOException.class);
        multiUserConfig.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(toFail(ifAnyOf("role:ROLE_USER")));
        throw new IOException("IO Fail");
    }
}
