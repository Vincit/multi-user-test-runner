package fi.vincit.multiusertest.runner.junit;

import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Set;

/**
 * Configuration for test MUTR runners
 */
public class RunnerConfig {
    private final Class<?> testClassType;
    private final Set<UserIdentifier> allowedIdentifiers;
    private final UserIdentifier producerIdentifier;
    private final UserIdentifier consumerIdentifier;
    private final FocusType focusType;

    public RunnerConfig(Class<?> testClassType, Set<UserIdentifier> allowedIdentifiers, UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier, FocusType focusType) {
        this.testClassType = testClassType;
        this.allowedIdentifiers = allowedIdentifiers;
        this.producerIdentifier = producerIdentifier;
        this.consumerIdentifier = consumerIdentifier;
        this.focusType = focusType;
    }

    /**
     * Type of the test class
     * @return Test class type
     */
    public Class<?> getTestClassType() {
        return testClassType;
    }

    /**
     * A complete set of allowed user identifiers used in the tests. This includes
     * both producer and consumer identifiers.
     * @return Set of allowed user identifiers.
     */
    public Set<UserIdentifier> getAllowedIdentifiers() {
        return allowedIdentifiers;
    }

    /**
     * Currently active producer user identifier
     * @return Producer identifier
     */
    public UserIdentifier getProducerIdentifier() {
        return producerIdentifier;
    }

    /**
     * Currently active consumer user identifier
     * @return Consumer identifier
     */
    public UserIdentifier getConsumerIdentifier() {
        return consumerIdentifier;
    }

    /**
     * Currently active focus mode typw
     * @return Focus mode type
     */
    public FocusType getFocusType() {
        return focusType;
    }
}

