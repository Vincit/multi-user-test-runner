package fi.vincit.multiusertest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.runners.model.FrameworkMethod;

import fi.vincit.multiusertest.annotation.TestUsers;

public class TestMethodFilter {
    private UserIdentifier creatorIdentifier;
    private UserIdentifier userIdentifier;

    public TestMethodFilter(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
    }

    public boolean shouldRun(FrameworkMethod frameworkMethod) {
        TestConfiguration configuration =
                TestConfiguration.fromTestUsers(frameworkMethod.getAnnotation(TestUsers.class));
        Collection<UserIdentifier> creators = configuration.getCreatorIdentifiers();
        Collection<UserIdentifier> users = configuration.getUserIdentifiers();

        boolean shouldRun = true;
        if (creatorIdentifier != null && !creators.isEmpty()) {
            shouldRun = creators.contains(creatorIdentifier);
        }
        if (userIdentifier != null && !users.isEmpty()) {
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
