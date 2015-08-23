package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.junit.runners.model.FrameworkMethod;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.TestUsers;

public class TestMethodFilter {
    private final UserIdentifier creatorIdentifier;
    private final UserIdentifier userIdentifier;

    public TestMethodFilter(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        Objects.requireNonNull(creatorIdentifier);
        Objects.requireNonNull(userIdentifier);

        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
    }

    public boolean shouldRun(FrameworkMethod frameworkMethod) {
        TestConfiguration configuration =
                TestConfiguration.fromTestUsers(
                        frameworkMethod.getAnnotation(TestUsers.class),
                        Optional.<MultiUserTestConfig>empty()
                );

        Collection<UserIdentifier> creators = configuration.getCreatorIdentifiers();
        Collection<UserIdentifier> users = configuration.getUserIdentifiers();

        boolean shouldRun = true;
        if (!creators.isEmpty()) {
            shouldRun = creators.contains(creatorIdentifier);
        }
        if (!users.isEmpty()) {
            shouldRun = shouldRun && users.contains(userIdentifier);
        }

        return shouldRun;
    }

    public List<FrameworkMethod> filter(List<FrameworkMethod> methods) {
        List<FrameworkMethod> methodsToRun = new ArrayList<>();
        for (FrameworkMethod frameworkMethod : methods) {
            if (shouldRun(frameworkMethod)) {
                methodsToRun.add(frameworkMethod);
            }
        }
        return methodsToRun;
    }
}
