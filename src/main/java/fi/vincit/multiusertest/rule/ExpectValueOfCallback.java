package fi.vincit.multiusertest.rule;

public interface ExpectValueOfCallback<RETURN_TYPE> {
    RETURN_TYPE doIt();
}
