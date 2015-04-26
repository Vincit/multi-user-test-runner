package fi.vincit.multiusertest.annotation;

import fi.vincit.multiusertest.runner.SpringMultiUserTestClassRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation works with MultiUserTestRunner. The creators parameter defines which
 * roles are used to create content and users parameter defines which roles are used to
 * consume content. The syntax and possible values for defining users are same in
 * {@link fi.vincit.multiusertest.runner.MultiUserTestRunner} was described.
 *
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface TestUsers {

    /**
     * Login as creator user
     */
    String CREATOR = "__FI_VINCIT_MULTI_ROLE_TEST_CREATOR__";
    /**
     * Login as a new user that has the same role as the creator
     */
    String NEW_USER = "__FI_VINCIT_MULTI_ROLE_TEST_NEW_USER__";

    String[] creators() default {};
    String[] users() default {NEW_USER};
    Class runner() default SpringMultiUserTestClassRunner.class;
}
