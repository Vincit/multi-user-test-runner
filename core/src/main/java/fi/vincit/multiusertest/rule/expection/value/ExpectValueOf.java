package fi.vincit.multiusertest.rule.expection.value;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

public class ExpectValueOf<VALUE_TYPE> implements Expectation {

    private final ReturnValueCall<VALUE_TYPE> callback;

    private final Map<UserIdentifier, ValueOfInfo<VALUE_TYPE>> expectations = new HashMap<>();

    public ExpectValueOf(ReturnValueCall<VALUE_TYPE> callback) {
        this.callback = callback;
    }

    /**
     * Expect return value of the function call to be the given value for the given users. If the
     * return value is not the expected value, then {@link AssertionError} will be thrown.
     * @param value Expected return value of the call
     * @param identifiers A set of user identifiers for which the comparison is made
     * @return ExpectValueOf object for chaining
     * @throws AssertionError when assertion fails.
     */
    public ExpectValueOf<VALUE_TYPE> toEqual(VALUE_TYPE value, UserIdentifiers identifiers) {
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new ValueOfInfo<>(
                            Optional.ofNullable(value),
                            Optional.<AssertionCall<VALUE_TYPE>>empty())
            );
        }
        return this;
    }

    /**
     * Expect the given assertion to pass for the given function call for the given users. If assertion
     * fails, then AssertionError will be thrown. If assertion callback throws an error it will be passed
     * through as is.
     * @param assertionCallback Function which makes the assertion
     * @param identifiers A set of user identifiers for which the assertion is made
     * @return ExpectValueOf object for chaining
     */
    public ExpectValueOf<VALUE_TYPE> toAssert(AssertionCall<VALUE_TYPE> assertionCallback, UserIdentifiers identifiers) {
        Objects.requireNonNull(assertionCallback, "Assertion callback must not be null");
        Objects.requireNonNull(assertionCallback, "Identifiers must not be null");

        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new ValueOfInfo<>(
                            Optional.<VALUE_TYPE>empty(),
                            Optional.of(assertionCallback))
            );
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier identifier) throws Throwable {
        VALUE_TYPE returnValue = callback.call();

        if (!expectations.containsKey(identifier)) {
            return;
        }

        ValueOfInfo<VALUE_TYPE> info = expectations.get(identifier);
        if (info.getAssertionCallback().isPresent()) {
            info.getAssertionCallback().get().call(returnValue);
        } else {
            assertThat(returnValue, is(info.getValue().orElse(null)));
        }
    }

    @Override
    public void setExpectedException(Class<? extends Throwable> expectedException) {
        // NOOP, doesn't use exceptions
    }
}
