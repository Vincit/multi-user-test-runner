package fi.vincit.multiusertest.util;

/**
 * Role in which user should be logged in in the test
 */
public enum LoginRole {
    /**
     * Producer creates content and is logged in by default at
     * the beginning of a test method
     */
    PRODUCER,
    /**
     * Consumer is the user who uses the system under test. This
     * user has to logged in manually during the test.
     */
    CONSUMER
}
