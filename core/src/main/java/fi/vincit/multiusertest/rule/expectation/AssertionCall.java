package fi.vincit.multiusertest.rule.expectation;

/**
 * Function call with a parameter to assert
 * @param <VALUE_TYPE> Value type
 * @since 1.0
 */
public interface AssertionCall<VALUE_TYPE> {
    void call(VALUE_TYPE value) throws Throwable;
}
