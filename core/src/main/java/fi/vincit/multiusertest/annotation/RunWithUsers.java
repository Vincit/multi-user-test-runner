package fi.vincit.multiusertest.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;


/**
 * <p>
 * This annotation works with MultiUserTestRunner JUnit runner. The creators parameter defines which
 * roles are used to create content and users parameter defines which roles are used to
 * consume content. The syntax and possible values for defining users are same as in
 * {@link MultiUserTestRunner}.
 * </p>
 * <p>
 * Annotation can also be user with methods. Then the annotation will define with what users the method
 * will be executed. If {@link RunWithUsers#producers()} are set, the method will only be executed if any of the specified creators are
 * being used as the creator. Same applies for the {@link RunWithUsers#consumers()}. If both creators and users are defined then
 * the method will be run only if any combination of creators and users are being used.
 * </p>
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface RunWithUsers {

    /**
     * Login as producer user
     */
    String PRODUCER = "__FI_VINCIT_MULTI_ROLE_TEST_CREATOR__";
    /**
     * Login as a new user that has the same role as the creator.
     * Can't be used if creator role uses an existing user.
     */
    String WITH_PRODUCER_ROLE = "__FI_VINCIT_MULTI_ROLE_TEST_NEW_USER__";

    /**
     * Don't login at all or clear login details
     */
    String ANONYMOUS = "__FI_VINCIT_MULTI_ROLE_TEST_ANONYMOUS__";

    /**
     * Producer roles/users to be used
     * @return users/roles
     */
    String[] producers() default {};

    /**
     * Consumer roles/users to be used
     * @return users/roles
     */
    String[] consumers() default {};

}
