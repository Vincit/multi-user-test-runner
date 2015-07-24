package fi.vincit.multiusertest.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import fi.vincit.multiusertest.annotation.TestUsers;

public class TestConfiguration {

    private Collection<UserIdentifier> creatorIdentifiers;
    private Collection<UserIdentifier> userIdentifiers;
    private Optional<Class<?>> runner;

    public static TestConfiguration fromTestUsers(TestUsers testUsers) {
        if (testUsers != null) {
            return new TestConfiguration(
                    getDefinitions(testUsers.creators()),
                    getDefinitions(testUsers.users()),
                    testUsers.runner()
            );
        } else {
            return new TestConfiguration();
        }
    }

    private static Collection<UserIdentifier> getDefinitions(String[] definitions) {
        if (definitions != null) {
            Collection<UserIdentifier> userIdentifiers = new LinkedHashSet<>();
            for (String user : definitions) {
                userIdentifiers.add(UserIdentifier.parse(user));
            }
            return userIdentifiers;
        } else {
            return Collections.emptySet();
        }
    }

    TestConfiguration() {
        this.creatorIdentifiers = Collections.emptySet();
        this.userIdentifiers = Collections.emptySet();
        this.runner = Optional.empty();
    }

    TestConfiguration(Collection<UserIdentifier> creatorIdentifiers, Collection<UserIdentifier> userIdentifiers, Class<?> runner) {
        this.creatorIdentifiers = creatorIdentifiers;
        this.userIdentifiers = userIdentifiers;
        this.runner = Optional.<Class<?>>ofNullable(runner);
    }

    public Collection<UserIdentifier> getCreatorIdentifiers() {
        return creatorIdentifiers;
    }

    public Collection<UserIdentifier> getUserIdentifiers() {
        return userIdentifiers;
    }

    public Optional<Class<?>> getRunner() {
        return runner;
    }
}
