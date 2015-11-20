package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.annotation.TestUsers;
import org.junit.runners.model.FrameworkMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TestMethodFilter {
    private final UserIdentifier producerIdentifier;
    private final UserIdentifier consumerIdentifier;

    public TestMethodFilter(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        Objects.requireNonNull(producerIdentifier);
        Objects.requireNonNull(consumerIdentifier);

        this.producerIdentifier = producerIdentifier;
        this.consumerIdentifier = consumerIdentifier;
    }

    public boolean shouldRun(FrameworkMethod frameworkMethod) {

        Optional<TestUsers> testUsersAnnotation =
                Optional.ofNullable(frameworkMethod.getAnnotation(TestUsers.class));
        Optional<RunWithUsers> runWithUsersAnnotation =
                Optional.ofNullable(frameworkMethod.getAnnotation(RunWithUsers.class));

        TestConfiguration configuration;
        if (runWithUsersAnnotation.isPresent()) {
            configuration = TestConfiguration.fromRunWithUsers(
                    runWithUsersAnnotation,
                    Optional.<MultiUserTestConfig>empty()
            );
        } else {
            configuration = TestConfiguration.fromTestUsers(
                    testUsersAnnotation,
                    Optional.<MultiUserTestConfig>empty()
            );
        }

        Collection<UserIdentifier> producers = configuration.getProducerIdentifiers();
        Collection<UserIdentifier> consumers = configuration.getConsumerIdentifiers();

        boolean shouldRun = true;
        if (!producers.isEmpty()) {
            shouldRun = producers.contains(producerIdentifier);
        }
        if (!consumers.isEmpty()) {
            shouldRun = shouldRun && consumers.contains(consumerIdentifier);
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
