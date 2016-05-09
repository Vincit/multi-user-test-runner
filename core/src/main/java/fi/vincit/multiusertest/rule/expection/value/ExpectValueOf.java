package fi.vincit.multiusertest.rule.expection.value;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.AbstractExpectation;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.rule.expection.call.ExceptionAssertionCall;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.Objects;

import static fi.vincit.multiusertest.rule.expection.call.ExpectCall.NOOP_ASSERTION;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Use {@link fi.vincit.multiusertest.rule.expection.Expectations} to create instances.
 * @param <VALUE_TYPE> Type of the value
 */
public class ExpectValueOf<VALUE_TYPE> extends AbstractExpectation<ValueOfInfo<VALUE_TYPE>> {

    private final ReturnValueCall<VALUE_TYPE> callback;



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
            getExpectations().put(identifier, new ValueOfInfo<>(
                            FailMode.EXPECT_NOT_FAIL,
                            Optional.ofNullable(value),
                            Optional.<AssertionCall<VALUE_TYPE>>empty(),
                            Optional.<Class<? extends Throwable>>empty(),
                            NOOP_ASSERTION)
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
            getExpectations().put(identifier, new ValueOfInfo<>(
                            FailMode.EXPECT_NOT_FAIL,
                            Optional.<VALUE_TYPE>empty(),
                            Optional.of(assertionCallback),
                            Optional.<Class<? extends Throwable>>empty(),
                            NOOP_ASSERTION)
            );
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier identifier) throws Throwable {
        Optional<Throwable> exceptionOnAssert = Optional.empty();
        try {
            VALUE_TYPE returnValue = callback.call();

            if (!getExpectations().containsKey(identifier)) {
                return;
            }

            ValueOfInfo<VALUE_TYPE> info = getExpectations().get(identifier);
            if (info.getAssertionCallback().isPresent()) {
                try {
                    info.getAssertionCallback().get().call(returnValue);
                } catch(Throwable t) {
                    exceptionOnAssert = Optional.of(t);
                }
            } else {
                assertThat(returnValue, is(info.getValue().orElse(null)));
            }
        } catch (Throwable e) {
            throwIfExpectationNotExpected(identifier, e);
            return;
        }
        if (exceptionOnAssert.isPresent()) {
            throw exceptionOnAssert.get();
        }

        throwIfExceptionIsExpected(identifier);
    }


    public ExpectValueOf<VALUE_TYPE> toFail(UserIdentifiers identifiers) {
        Objects.requireNonNull(identifiers, "User identifiers must not be null");

        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            getExpectations().put(identifier, new ValueOfInfo<>(
                    FailMode.EXPECT_FAIL,
                    Optional.<VALUE_TYPE>empty(),
                    Optional.<AssertionCall<VALUE_TYPE>>empty(),
                    Optional.<Class<? extends Throwable>>empty(),
                    NOOP_ASSERTION)
            );
        }
        return this;
    }

    public ExpectValueOf<VALUE_TYPE> toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers) {
        return toFailWithException(exception, identifiers, NOOP_ASSERTION);
    }

    public ExpectValueOf<VALUE_TYPE> toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers, ExceptionAssertionCall exceptionAssertionCall) {
        Objects.requireNonNull(exception, "Exception must not be null");
        Objects.requireNonNull(exceptionAssertionCall, "ExceptionAssertionCall must not be null");
        Objects.requireNonNull(identifiers, "Identifiers must not be null");

        setGeneralFailMode(FailMode.EXPECT_FAIL);
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            getExpectations().put(identifier, new ValueOfInfo<>(
                    FailMode.EXPECT_FAIL,
                    Optional.<VALUE_TYPE>empty(),
                    Optional.<AssertionCall<VALUE_TYPE>>empty(),
                    Optional.<Class<? extends Throwable>>of(exception),
                    exceptionAssertionCall)
            );
        }
        return this;
    }
}
