package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractWhenThen<T extends TestExpectation> implements WhenThen<T> {

    private static final String DEBUG_LOG_INDENT = "  ";
    private final Set<UserIdentifier> currentIdentifiers = new HashSet<>();
    private final Set<UserIdentifier> currentProducerIdentifiers = new HashSet<>();
    private final Map<ConsumerProducerSet, T> expectationsByIdentifier = new HashMap<>();

    private final UserIdentifier userIdentifier;
    private final UserIdentifier producerIdentifier;
    private final Authorization authorizationRule;
    private final UserRoleIT userRoleIT;
    private T defaultExpectation;
    private Consumer<String> debugLogger;

    public AbstractWhenThen(UserIdentifier producerIdentifier, UserIdentifier userIdentifier, Authorization authorizationRule, UserRoleIT userRoleIT) {
        this.userIdentifier = userIdentifier;
        this.producerIdentifier = producerIdentifier;
        this.authorizationRule = authorizationRule;
        this.userRoleIT = userRoleIT;
    }
    
    @Override
    public ThenProducer<T> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers) {
        return whenCalledWithIdentifiers(UserIdentifiers.listOf(userIdentifiers));
    }

    @Override
    public ThenProducer<T> whenCalledWithAnyOf(UserIdentifier... userIdentifiers) {
        return whenCalledWithIdentifiers(Arrays.asList(userIdentifiers));
    }

    @Override
    public ThenProducer<T> whenCalledWithAnyOf(Collection<UserIdentifier> userIdentifiers) {
        return whenCalledWithIdentifiers(userIdentifiers);
    }

    @Override
    public ThenProducer<T> whenCalledWithAnyOf(Supplier<Collection<UserIdentifier>> userIdentifierSupplier) {
        return whenCalledWithAnyOf(userIdentifierSupplier.get());
    }

    @Override
    public When<T> whenProducerIsAny() {
        return whenProducerIsAnyOf(Collections.emptyList());
    }

    @Override
    public When<T> whenProducerIsAnyOf(UserIdentifierCollection... producerIdentifiers) {
        return whenCalledWithProducerIdentifiers(UserIdentifiers.listOf(producerIdentifiers));
    }

    @Override
    public When<T> whenProducerIsAnyOf(UserIdentifier... producerIdentifiers) {
        return whenCalledWithProducerIdentifiers(Arrays.asList(producerIdentifiers));
    }

    @Override
    public When<T> whenProducerIsAnyOf(Collection<UserIdentifier> producerIdentifiers) {
        return whenCalledWithProducerIdentifiers(producerIdentifiers);
    }

    @Override
    public When<T> whenProducerIsAnyOf(Supplier<Collection<UserIdentifier>> producerIdentifierSupplier) {
        return whenProducerIsAnyOf(producerIdentifierSupplier.get());
    }

    @Override
    public WhenThen<T> then(final T testExpectation) {
        Objects.requireNonNull(testExpectation, "testExpectation must not be null");

        if (currentIdentifiers.isEmpty()) {
            throw new IllegalStateException("Call whenCalledWithAnyOf before calling then method");
        }

        currentIdentifiers.forEach(identifier -> {
            if (currentProducerIdentifiers.isEmpty()) {
                final ConsumerProducerSet consumerProducerSet = new ConsumerProducerSet(null, identifier);
                addExpectation(testExpectation, consumerProducerSet);
            } else {
                currentProducerIdentifiers.forEach(producer -> {
                    final ConsumerProducerSet consumerProducerSet = new ConsumerProducerSet(producer, identifier);
                    addExpectation(testExpectation, consumerProducerSet);
                });
            }
        });

        return this;
    }

    private void addExpectation(T testExpectation, ConsumerProducerSet consumerProducerSet) {
        if (expectationsByIdentifier.containsKey(consumerProducerSet)) {
            throw new IllegalStateException(
                    String.format("User identifier %s already has expectation",
                            consumerProducerSet.toString()
                    )
            );
        } else {
            expectationsByIdentifier.put(
                    consumerProducerSet,
                    testExpectation
            );
        }
    }

    @Override
    public WhenThen<T> otherwise(T testExpectation) {
        Objects.requireNonNull(testExpectation, "testExpectation must not be null");
        this.defaultExpectation = testExpectation;
        return this;
    }

    @Override
    public WhenThen<T> byDefault(T testExpectation) {
        return otherwise(testExpectation);
    }

    @Override
    public WhenThen<T> debugRoleMappings(Consumer<String> logger) {
        this.debugLogger = logger;
        return this;
    }

    @Override
    public void test() throws Throwable {
        printRoleDebugLog();

        final ConsumerProducerSet consumerProducerSet = new ConsumerProducerSet(producerIdentifier, userIdentifier);
        T testExpectation = expectationsByIdentifier.computeIfAbsent(
                consumerProducerSet,
                this::getDefinedDefaultException
        );

        this.authorizationRule.markExpectationConstructed();
        this.userRoleIT.logInAs(LoginRole.CONSUMER);
        this.test(testExpectation, consumerProducerSet);
        this.userRoleIT.logInAs(LoginRole.PRODUCER);
    }

    protected abstract void test(T testExpectation, ConsumerProducerSet consumerProducerSet) throws Throwable;

    protected abstract T getDefaultExpectation(ConsumerProducerSet consumerProducerSet);

    private T getDefinedDefaultException(ConsumerProducerSet consumerProducerSet) {
        return Optional.ofNullable(defaultExpectation)
                .orElseGet(() -> getDefaultExpectation(consumerProducerSet));
    }

    private WhenThen<T> whenCalledWithIdentifiers(Collection<UserIdentifier> userIdentifiers) {
        currentIdentifiers.clear();

        validateWhen(userIdentifiers);

        userIdentifiers.forEach(this::addCurrentUserIdentifiers);
        return this;
    }

    private WhenThen<T> whenCalledWithProducerIdentifiers(Collection<UserIdentifier> userIdentifiers) {
        currentIdentifiers.clear();
        currentProducerIdentifiers.clear();

        userIdentifiers.forEach(this::addCurrentProducerIdentifiers);
        return this;
    }

    private void addCurrentUserIdentifiers(UserIdentifier userIdentifier) {
        if (currentIdentifiers.contains(userIdentifier)) {
            throw new IllegalStateException("User identifier " + userIdentifier.toString() + " already set");
        }
        currentIdentifiers.add(userIdentifier);
    }

    private void addCurrentProducerIdentifiers(UserIdentifier userIdentifier) {
        if (currentProducerIdentifiers.contains(userIdentifier)) {
            throw new IllegalStateException("Producer identifier " + userIdentifier.toString() + " already set");
        }
        currentProducerIdentifiers.add(userIdentifier);
    }

    private void printRoleDebugLog() {
        if (debugLogger != null) {
            debugLogger.accept("Running with expectations:");
            expectationsByIdentifier.entrySet().stream()
                    .map(Object::toString)
                    .map(roleDebugLine -> DEBUG_LOG_INDENT + roleDebugLine)
                    .sorted()
                    .forEach(debugLogger);

            if (defaultExpectation != null) {
                debugLogger.accept("  otherwise=" + defaultExpectation.toString());
            } else {
                debugLogger.accept("  No otherwise condition set");
            }
        }
    }

    private static <T> void validateWhen(Collection<T> userIdentifiers) {
        if (userIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("At least one identifier must be defined");
        }
    }

    /*
    For unit tests
    */

    Set<UserIdentifier> getCurrentIdentifiers() {
        return currentIdentifiers;
    }

    Map<ConsumerProducerSet, T> getExpectationsByIdentifier() {
        return expectationsByIdentifier;
    }

}
