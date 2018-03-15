package fi.vincit.multiusertest.runner.junit5;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.jupiter.api.extension.*;

import java.util.Collections;
import java.util.List;

import static fi.vincit.multiusertest.util.TestNameUtil.resolveTestName;

public class TemplateInvocationContext implements TestTemplateInvocationContext {

    private final UserIdentifier producer;
    private final UserIdentifier consumer;

    public TemplateInvocationContext(UserIdentifier producer, UserIdentifier consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    public String getDisplayName(int invocationIndex) {
        return resolveTestName(producer, consumer);
    }

    @Override
    public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new MutrParameterResolver(producer, consumer));
    }

}
