package fi.vincit.multiusertest.rule.expection;

/**
 * Function call with a parameter to assert
 * @param <VALUE_TYPE> Value type
 */
public interface AssertionCall<VALUE_TYPE> {
    void call(VALUE_TYPE value);
}
