package fi.vincit.multiusertest.rule.expection.call;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.AbstractExpectation;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

import java.util.Objects;
import java.util.Optional;

import static fi.vincit.multiusertest.rule.FailMode.EXPECT_FAIL;

/**
 * <p>
 * Expect a function call to throw or not throw an exception. Default exception is
 * {@link fi.vincit.multiusertest.rule.AuthorizationRule}'s exception.
 * A custom exception can be given via {@link ExpectCall#toFailWithException(Class, UserIdentifiers)}
 * method.
 * </p>
 * <p>
 * toFail* and notToFail methods can't be mixed together. I.e. only <code>toFail(...).toFailWithException(...)</code> and
 * <code>notToFail(...).notToFail(...)</code> calls are allowed. <b>Not</b> <code>toFail(...).notToFail(...)</code>.
 * Otherwise it would be difficult to determine what to do with the definitions that are not defined since
 * the expectation doesn't have access to all available definitions.
 * </p>
 * <p>
 * Use {@link fi.vincit.multiusertest.rule.expection.Expectations} to create instances.
 * </p>
 */
public class ExpectCall extends AbstractExpectation<ExpectationInfo> implements ExpectCallFail, ExpectCallNotFail {

    public static final ExceptionAssertionCall NOOP_ASSERTION = new ExceptionAssertionCall() {
        @Override
        public void assertException(Throwable thrownException) {
        }
    };

    private final FunctionCall functionCall;

    public ExpectCall(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    @Override
    public ExpectCallFail toFail(UserIdentifiers identifiers) {
        Objects.requireNonNull(identifiers, "Identifiers must not be null");
        setGeneralFailMode(FailMode.EXPECT_FAIL);
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            getExpectations().put(identifier, new ExpectationInfo(
                    FailMode.EXPECT_FAIL,
                    Optional.<Class<? extends Throwable>>empty(),
                    NOOP_ASSERTION
            ));
        }
        return this;
    }

    @Override
    public ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers) {
        return toFailWithException(exception, identifiers, NOOP_ASSERTION);
    }

    @Override
    public ExpectCallFail toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers, ExceptionAssertionCall exceptionAssertionCall) {
        Objects.requireNonNull(exception, "Exception must not be null");
        Objects.requireNonNull(exceptionAssertionCall, "ExceptionAssertionCall must not be null");
        Objects.requireNonNull(identifiers, "Identifiers must not be null");

        setGeneralFailMode(FailMode.EXPECT_FAIL);
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            getExpectations().put(identifier, new ExpectationInfo(
                    EXPECT_FAIL,
                    Optional.<Class<? extends Throwable>>of(exception),
                    exceptionAssertionCall
            ));
        }
        return this;
    }

    @Override
    public ExpectCallNotFail notToFail(UserIdentifiers identifiers) {
        Objects.requireNonNull(identifiers, "Identifiers must not be null");
        setGeneralFailMode(FailMode.EXPECT_NOT_FAIL);

        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            getExpectations().put(identifier, new ExpectationInfo(FailMode.EXPECT_NOT_FAIL, Optional.<Class<? extends Throwable>>empty(), NOOP_ASSERTION));
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier userIdentifier) throws Throwable {
        try {
            functionCall.call();
        } catch (Throwable e) {
            throwIfExpectationNotExpected(userIdentifier, e);
            return;
        }
        throwIfExceptionIsExpected(userIdentifier);
    }

}
