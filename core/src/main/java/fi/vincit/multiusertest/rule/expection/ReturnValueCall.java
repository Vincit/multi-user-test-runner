package fi.vincit.multiusertest.rule.expection;

/**
 * Function call the returns a value
 * @param <RETURN_TYPE> Return value type
 */
public interface ReturnValueCall<RETURN_TYPE> {
    RETURN_TYPE call() throws Throwable;
}
