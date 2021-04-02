package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.test.MultiUserConfig;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import static fi.vincit.multiusertest.util.ConfigurationUtil.getConfigComponent;

class MutrParameterResolver implements ParameterResolver {

    private final UserIdentifier producer;
    private final UserIdentifier consumer;

    public MutrParameterResolver(UserIdentifier producer, UserIdentifier consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {
        return parameterContext.getParameter().getType().equals(Authorization.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext,
            ExtensionContext extensionContext) {
        final JUnit5Authorization authorization = new JUnit5Authorization();
        final MultiUserConfig userIt =
                getConfigComponent(extensionContext.getRequiredTestInstance());

        userIt.setUsers(producer, consumer);
        userIt.setAuthorizationRule(authorization);
        userIt.initialize();

        authorization.setRole(producer, null);

        userIt.logInAs(LoginRole.PRODUCER);

        return authorization;
    }
}
