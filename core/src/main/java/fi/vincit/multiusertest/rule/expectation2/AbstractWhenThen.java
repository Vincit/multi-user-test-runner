package fi.vincit.multiusertest.rule.expectation2;

import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.*;

public abstract class AbstractWhenThen<T extends TestExpectation> implements WhenThen<T> {

    private final Set<UserIdentifier> currentIdentifiers = new HashSet<>();
    private final Map<UserIdentifier, T> expectationsByIdentifier = new HashMap<>();

    private final UserIdentifier userIdentifier;
    private final AuthorizationRule authorizationRule;
    private T defaultExpectation;

    public AbstractWhenThen(UserIdentifier userIdentifier, AuthorizationRule authorizationRule) {
        this.userIdentifier = userIdentifier;
        this.authorizationRule = authorizationRule;
    }

    @Override
    public WhenThen<T> whenCalledWith(UserIdentifiers... userIdentifiers) {
        currentIdentifiers.clear();

        validateWhen(userIdentifiers);

        for (UserIdentifiers identifiers : userIdentifiers) {
            identifiers.getIdentifiers().forEach(this::addCurrentUserIdentifiers);
        }
        return this;
    }

    @Override
    public Then<T> whenCalledWith(UserIdentifier... userIdentifiers) {
        currentIdentifiers.clear();

        validateWhen(userIdentifiers);

        addCurrentUserIdentifiers(userIdentifiers);

        return this;
    }

    @Override
    public Then<T> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers) {
        return whenCalledWith(UserIdentifiers.anyOf(userIdentifiers));
    }

    @Override
    public Then<T> whenCalledWithAnyOf(String... userIdentifiers) {
        return whenCalledWith(UserIdentifiers.anyOf(userIdentifiers));
    }

    private void addCurrentUserIdentifiers(UserIdentifier... userIdentifiers) {
        for (UserIdentifier identifier : userIdentifiers) {
            if (currentIdentifiers.contains(identifier)) {
                throw new IllegalStateException("User identifier " + identifier + " already set");
            }
            currentIdentifiers.add(identifier);
        }
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
        this.test(testExpectation, userIdentifier);
    }

    protected abstract void test(T testExpectation, UserIdentifier userIdentifier) throws Throwable;

    protected abstract T getDefaultExpectation(UserIdentifier userIdentifier);

    protected T getDefinedDefaultException(UserIdentifier userIdentifier) {
        return Optional.ofNullable(defaultExpectation)
                .orElseGet(() -> getDefaultExpectation(userIdentifier));
    }

    private static <T> void validateWhen(T[] userIdentifiers) {
        if (userIdentifiers.length == 0) {
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
