package fi.vincit.multiusertest.rule.expectation;

/**
 * Function call the returns a value
 * @param <RETURN_TYPE> Return value type
 * @since 1.0
 */
public interface ReturnValueCall<RETURN_TYPE> {
    RETURN_TYPE call() throws Throwable;
}
