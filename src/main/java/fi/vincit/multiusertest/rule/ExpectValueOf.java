package fi.vincit.multiusertest.rule;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;

public class ExpectValueOf<VALUE_TYPE> {

    private ExpectValueOfCallback<VALUE_TYPE> callback;

    private static class Info<VALUE_TYPE> {
        final Authentication.Identifiers identifiers;
        final Optional<VALUE_TYPE> value;
        final Optional<Callback<VALUE_TYPE>> assertionCallback;

        public Info(Authentication.Identifiers identifiers, Optional<VALUE_TYPE> value, Optional<Callback<VALUE_TYPE>> assertionCallback) {
            this.identifiers = identifiers;
            this.value = value;
            this.assertionCallback = assertionCallback;
        }

        public Authentication.Identifiers getIdentifiers() {
            return identifiers;
        }

        public Optional<VALUE_TYPE> getValue() {
            return value;
        }

        public Optional<Callback<VALUE_TYPE>> getAssertionCallback() {
            return assertionCallback;
        }
    }

    public Map<String, Info<VALUE_TYPE>> expectations = new HashMap<>();

    public ExpectValueOf(ExpectValueOfCallback<VALUE_TYPE> callback) {
        this.callback = callback;
    }

    public ExpectValueOf toEqual(VALUE_TYPE value, Authentication.Identifiers identifiers) {
        for (String identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info<>(
                    identifiers,
                    Optional.ofNullable(value),
                    Optional.<Callback<VALUE_TYPE>>empty())
            );
        }
        return this;
    }

    public ExpectValueOf toAssert(Callback<VALUE_TYPE> assertionCallback, Authentication.Identifiers identifiers) {
        for (String identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info<>(
                            identifiers,
                            Optional.<VALUE_TYPE>empty(),
                            Optional.of(callback))
            );
        }
        return this;
    }

    public void execute(UserIdentifier identifier) {
        VALUE_TYPE returnValue = callback.doIt();

        if (!expectations.containsKey(identifier.toString())) {
            return;
        }

        Info<VALUE_TYPE> info = expectations.get(identifier.toString());
        if (info.getAssertionCallback().isPresent()) {
            info.getAssertionCallback().get().doIt(returnValue);
        } else {
            assertThat(returnValue, is(info.getValue().orElse(null)));
        }
    }
}
