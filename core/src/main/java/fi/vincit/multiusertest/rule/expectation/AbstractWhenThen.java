package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.rule.Authorization;
import fi.vincit.multiusertest.test.UserRoleIT;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractWhenThen<T extends TestExpectation> implements WhenThen<T> {

    private final Set<UserIdentifier> currentIdentifiers = new HashSet<>();
    private final Map<UserIdentifier, T> expectationsByIdentifier = new HashMap<>();

    private final UserIdentifier userIdentifier;
    private final Authorization authorizationRule;
    private UserRoleIT userRoleIT;
    private T defaultExpectation;

    public AbstractWhenThen(UserIdentifier userIdentifier, Authorization authorizationRule, UserRoleIT userRoleIT) {
        this.userIdentifier = userIdentifier;
        this.authorizationRule = authorizationRule;
        this.userRoleIT = userRoleIT;
    }
    
    @Override
    public Then<T> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers) {
        return whenCalledWithIdentifiers(UserIdentifiers.listOf(userIdentifiers));
    }

    @Override
    public Then<T> whenCalledWithAnyOf(UserIdentifier... userIdentifiers) {
        return whenCalledWithIdentifiers(Arrays.asList(userIdentifiers));
    }

    @Override
    public Then<T> whenCalledWithAnyOf(Collection<UserIdentifier> userIdentifiers) {
        return whenCalledWithIdentifiers(userIdentifiers);
    }

    @Override
    public Then<T> whenCalledWithAnyOf(Supplier<Collection<UserIdentifier>> userIdentifierSupplier) {
        return whenCalledWithAnyOf(userIdentifierSupplier.get());
    }

    @Override
    public WhenThen<T> then(final T testExpectation) {
        Objects.requireNonNull(testExpectation, "testExpectation must not be null");

        if (currentIdentifiers.isEmpty()) {
            throw new IllegalStateException("Call whenCalledWith before calling then method");
        }

        try {
            currentIdentifiers.forEach(identifier -> {
                if (expectationsByIdentifier.containsKey(identifier)) {
                    throw new IllegalStateException(
                            String.format("User identifier %s already has expectation",
                                    identifier.toString()
                            )
                    );
                } else {
                    expectationsByIdentifier.put(
                            identifier,
                            testExpectation
                    );
                }
            });
        } finally {
            currentIdentifiers.clear();
        }

        return this;
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
    public void test() throws Throwable {
        T testExpectation = expectationsByIdentifier.computeIfAbsent(
                userIdentifier,
                this::getDefinedDefaultException
        );

        this.authorizationRule.markExpectationConstructed();
        this.userRoleIT.logInAs(LoginRole.CONSUMER);
        this.test(testExpectation, userIdentifier);
        this.userRoleIT.logInAs(LoginRole.PRODUCER);
    }

    protected abstract void test(T testExpectation, UserIdentifier userIdentifier) throws Throwable;

    protected abstract T getDefaultExpectation(UserIdentifier userIdentifier);

    private T getDefinedDefaultException(UserIdentifier userIdentifier) {
        return Optional.ofNullable(defaultExpectation)
                .orElseGet(() -> getDefaultExpectation(userIdentifier));
    }

    private WhenThen<T> whenCalledWithIdentifiers(Collection<UserIdentifier> userIdentifiers) {
        currentIdentifiers.clear();

        validateWhen(userIdentifiers);

        userIdentifiers.forEach(this::addCurrentUserIdentifiers);
        return this;
    }

    private void addCurrentUserIdentifiers(UserIdentifier userIdentifier) {
        if (currentIdentifiers.contains(userIdentifier)) {
            throw new IllegalStateException("User identifier " + userIdentifier.toString() + " already set");
        }
        currentIdentifiers.add(userIdentifier);
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

    Map<UserIdentifier, T> getExpectationsByIdentifier() {
        return expectationsByIdentifier;
    }

}
