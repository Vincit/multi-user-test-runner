package fi.vincit.multiusertest.rule.expection;

public interface AssertionCall<VALUE_TYPE> {
    void call(VALUE_TYPE value);
}
