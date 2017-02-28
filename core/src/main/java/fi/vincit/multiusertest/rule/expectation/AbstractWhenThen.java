package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.runner.junit5.Authorization;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifierCollection;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public abstract class AbstractWhenThen<T extends TestExpectation> implements WhenThen<T> {

    private final Set<UserIdentifier> currentIdentifiers = new HashSet<>();
    private final Map<UserIdentifier, T> expectationsByIdentifier = new HashMap<>();

    private final UserIdentifier userIdentifier;
    private final Authorization authorizationRule;
    private T defaultExpectation;

    public AbstractWhenThen(UserIdentifier userIdentifier, Authorization authorizationRule) {
        this.userIdentifier = userIdentifier;
        this.authorizationRule = authorizationRule;
    }

    @Override
    public Then<T> whenCalledWithAnyOf(UserIdentifierCollection... userIdentifiers) {
        final List<UserIdentifiers> allIdentifiers = Stream.of(userIdentifiers)
                .map(UserIdentifierCollection::getUserIdentifiers)
                .flatMap(Collection::stream)
                .map(UserIdentifiers::new)
                .collect(toList());

        return whenCalledWith(allIdentifiers);
    }

    @Override
    public Then<T> whenCalledWithAnyOf(UserIdentifier... userIdentifiers) {
        return whenCalledWithIdentifiers(Arrays.asList(userIdentifiers));
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

    private T getDefinedDefaultException(UserIdentifier userIdentifier) {
        return Optional.ofNullable(defaultExpectation)
                .orElseGet(() -> getDefaultExpectation(userIdentifier));
    }

    private WhenThen<T> whenCalledWith(List<UserIdentifiers> userIdentifiers) {
        currentIdentifiers.clear();

        validateWhen(userIdentifiers);

        for (UserIdentifiers identifiers : userIdentifiers) {
            identifiers.getIdentifiers().forEach(this::addCurrentUserIdentifiers);
        }
        return this;
    }

    private WhenThen<T> whenCalledWithIdentifiers(List<UserIdentifier> userIdentifiers) {
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

    private static <T> void validateWhen(List<T> userIdentifiers) {
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
