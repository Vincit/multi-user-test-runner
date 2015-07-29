package fi.vincit.multiusertest.rule.expection.value;

import fi.vincit.multiusertest.rule.expection.AssertionCall;
import fi.vincit.multiusertest.util.Optional;

class ValueOfInfo<VALUE_TYPE> {
    private final Optional<VALUE_TYPE> value;
    private final Optional<AssertionCall<VALUE_TYPE>> assertionCallback;

    public ValueOfInfo(Optional<VALUE_TYPE> value, Optional<AssertionCall<VALUE_TYPE>> assertionCallback) {
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
