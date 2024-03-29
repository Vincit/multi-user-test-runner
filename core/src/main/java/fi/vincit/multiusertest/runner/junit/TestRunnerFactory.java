package fi.vincit.multiusertest.runner.junit;

import fi.vincit.multiusertest.util.FocusType;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.TestClass;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Creates instances of JUnit test runners
 */
public class TestRunnerFactory {

    private final TestClass testClass;
    private final Constructor runnerConstructor;

    /**
     *
     * @param testClass Test class
     * @param runnerConstructor Test class constructor
     */
    public TestRunnerFactory(TestClass testClass, Constructor runnerConstructor) {
        this.testClass = testClass;
        this.runnerConstructor = runnerConstructor;
    }

    /**
     * Creates runners for each producer consumer combination.
     * @param producerIdentifiers Producer identifiers
     * @param consumerIdentifiers Consumer identifiers
     * @return All required combinations for given identifiers
     * @throws Exception If an exception is thrown
     */
    public List<Runner> createRunnersForRoles(Collection<UserIdentifier> producerIdentifiers, Collection<UserIdentifier> consumerIdentifiers, FocusType focusType) throws Exception {
        List<Runner> runners = new ArrayList<>();
        if (consumerIdentifiers.isEmpty()) {
            consumerIdentifiers.add(UserIdentifier.getWithProducerRole(FocusType.NONE));
        }
        validateProducers(producerIdentifiers);
        validateConsumers(producerIdentifiers, consumerIdentifiers);

        Set<UserIdentifier> allowedIdentifiers = new HashSet<>();
        allowedIdentifiers.addAll(producerIdentifiers);
        allowedIdentifiers.addAll(consumerIdentifiers);

        for (UserIdentifier producerIdentifier : producerIdentifiers) {
            for (UserIdentifier consumerIdentifier : consumerIdentifiers) {
                final RunnerConfig runnerConfig = new RunnerConfig(
                        testClass.getJavaClass(),
                        allowedIdentifiers,
                        producerIdentifier,
                        consumerIdentifier,
                        focusType
                );

                final Object parentRunner = runnerConstructor.newInstance(runnerConfig);

                runners.add((ParentRunner) parentRunner);

            }
        }
        return runners;
    }

    void validateConsumers(Collection<UserIdentifier> producerIdentifiers, Collection<UserIdentifier> consumerIdentifiers) {
        boolean containsExistingUserDefinition = false;
        for (UserIdentifier identifier : producerIdentifiers) {
            if (identifier.getType() == UserIdentifier.Type.USER) {
                containsExistingUserDefinition = true;
            }
        }

        if (containsExistingUserDefinition
                && consumerIdentifiers.contains(UserIdentifier.getWithProducerRole(FocusType.NONE))) {
            throw new IllegalArgumentException("User definitions can't contain WITH_PRODUCER_ROLE when producers have a 'user' definition");
        }
    }

    void validateProducers(Collection<UserIdentifier> producerIdentifiers) {
        if (producerIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("Producer must be specified");
        }

        if (producerIdentifiers.contains(UserIdentifier.getProducer(FocusType.NONE))) {
            throw new IllegalArgumentException("Producer can't use PRODUCER role");
        }

        if (producerIdentifiers.contains(UserIdentifier.getWithProducerRole(FocusType.NONE))) {
            throw new IllegalArgumentException("Producer can't use WITH_PRODUCER_ROLE role");
        }
    }

}
