package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.TestUsers;
import org.junit.runners.model.FrameworkMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CheckShouldRun {
    private UserIdentifier creatorIdentifier;
    private UserIdentifier userIdentifier;

    public CheckShouldRun(UserIdentifier creatorIdentifier, UserIdentifier userIdentifier) {
        this.creatorIdentifier = creatorIdentifier;
        this.userIdentifier = userIdentifier;
    }

    public boolean shouldRun(FrameworkMethod frameworkMethod) {
        Set<UserIdentifier> creators = AnnotationUtil.getCreators(frameworkMethod.getAnnotation(TestUsers.class));
        Set<UserIdentifier> users = AnnotationUtil.getUsers(frameworkMethod.getAnnotation(TestUsers.class));

        boolean shouldRun = true;
        if (!creators.isEmpty()) {
            shouldRun = creators.contains(creatorIdentifier);
        }
        if (!users.isEmpty()) {
            shouldRun = shouldRun && users.contains(userIdentifier);
        }

        return shouldRun;
    }

    public List<FrameworkMethod> getMethodsToRun(List<FrameworkMethod> methods) {
        List<FrameworkMethod> methodsToRun = new ArrayList<>();
        for (FrameworkMethod frameworkMethod : methods) {
            if (shouldRun(frameworkMethod)) {
                methodsToRun.add(frameworkMethod);
            }
        }
        return methodsToRun;
    }
}
