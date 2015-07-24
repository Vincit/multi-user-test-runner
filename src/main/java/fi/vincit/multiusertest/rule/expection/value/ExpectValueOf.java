package fi.vincit.multiusertest.rule.expection.value;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import fi.vincit.multiusertest.rule.Authentication;
import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.rule.expection.ReturnValueCall;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ExpectValueOf<VALUE_TYPE> implements Expectation {

    private ReturnValueCall<VALUE_TYPE> callback;

    private static class Info<VALUE_TYPE> {
        final Optional<VALUE_TYPE> value;
        final Optional<AssertionCall<VALUE_TYPE>> assertionCallback;

        public Info(Optional<VALUE_TYPE> value, Optional<AssertionCall<VALUE_TYPE>> assertionCallback) {
            this.value = value;
            this.assertionCallback = assertionCallback;
        }

        public Optional<VALUE_TYPE> getValue() {
            return value;
        }

        public Optional<AssertionCall<VALUE_TYPE>> getAssertionCallback() {
            return assertionCallback;
        }
    }

    public Map<String, Info<VALUE_TYPE>> expectations = new HashMap<>();

    public ExpectValueOf(ReturnValueCall<VALUE_TYPE> callback) {
        this.callback = callback;
    }

    public ExpectValueOf<VALUE_TYPE> toEqual(VALUE_TYPE value, Authentication.Identifiers identifiers) {
        for (String identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info<>(
                            Optional.ofNullable(value),
                            Optional.<AssertionCall<VALUE_TYPE>>empty())
            );
        }
        return this;
    }

    public ExpectValueOf<VALUE_TYPE> toAssert(AssertionCall<VALUE_TYPE> assertionCallback, Authentication.Identifiers identifiers) {
        for (String identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info<>(
                            Optional.<VALUE_TYPE>empty(),
                            Optional.of(assertionCallback))
            );
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier identifier) throws Throwable {
        VALUE_TYPE returnValue = callback.call();

        if (!expectations.containsKey(identifier.toString())) {
            return;
        }

        Info<VALUE_TYPE> info = expectations.get(identifier.toString());
        if (info.getAssertionCallback().isPresent()) {
            info.getAssertionCallback().get().call(returnValue);
        } else {
            assertThat(returnValue, is(info.getValue().orElse(null)));
        }
    }
}
