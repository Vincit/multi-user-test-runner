package fi.vincit.multiusertest.annotation;

import fi.vincit.multiusertest.rule.EmptyUserDefinitionClass;
import fi.vincit.multiusertest.rule.UserDefinitionClass;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * <p>
 * This annotation works with MultiUserTestRunner JUnit runner. The producer parameter defines which
 * roles are used to create content and consumers parameter defines which roles are used to
 * consume content. The syntax and possible values for defining users are same as in
 * {@link MultiUserTestRunner}.
 * </p>
 * <p>
 * At least one producer role/user has to be defined. This can be done via array of strings
 * ({@link RunWithUsers#producers()}) or using a user definition class
 * ({@link RunWithUsers#producerClass()}}).
 * </p>
 * <p>
 * If user definitions are given with both string array and {@link UserDefinitionClass} are given the
 * user definitions are merged. This enables to reuse {@link UserDefinitionClass} more.
 * </p>
 * <p>
 * Annotation can also be user with methods. Then the annotation will define with what users the method
 * will be executed. If {@link RunWithUsers#producers()} are set, the method will only be executed if any of the specified producers are
 * being used as the producer. Same applies for the {@link RunWithUsers#consumers()}. If both producers and consumers are defined then
 * the method will be run only if any combination of producers and users are being used.
 * </p>
 *
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface RunWithUsers {

    /**
     * Login as producer user
     */
    String PRODUCER = "__FI_VINCIT_MULTI_ROLE_TEST_CREATOR__";
    /**
     * Login as producer user. Focused variant
     */
    String $PRODUCER = "$__FI_VINCIT_MULTI_ROLE_TEST_CREATOR__";

    /**
     * Login as a new user that has the same role as the producer.
     * Can't be used if producer role uses an existing user.
     */
    String WITH_PRODUCER_ROLE = "__FI_VINCIT_MULTI_ROLE_TEST_NEW_USER__";
    /**
     * Login as a new user that has the same role as the producer.
     * Can't be used if producer role uses an existing user. Focused variant
     */
    String $WITH_PRODUCER_ROLE = "$__FI_VINCIT_MULTI_ROLE_TEST_NEW_USER__";

    /**
     * Don't login at all or clear login details
     */
    String ANONYMOUS = "__FI_VINCIT_MULTI_ROLE_TEST_ANONYMOUS__";
    /**
     * Don't login at all or clear login details. Focused variant
     */
    String $ANONYMOUS = "$__FI_VINCIT_MULTI_ROLE_TEST_ANONYMOUS__";

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

    /**
     * Producer roles/users to be used from a user definition class
     * @return Class defining user definitions
     * @since 0.6
     */
    Class<? extends UserDefinitionClass> producerClass() default EmptyUserDefinitionClass.class;

    /**
     * Consumer roles/users to be used from a user definition class
     * @return Class defining user definitions
     * @since 0.6
     */
    Class<? extends UserDefinitionClass> consumerClass() default EmptyUserDefinitionClass.class;

    /**
     * Turns on focus mode where <pre>@</pre> prefix in user identifier is interpreted so that
     * tests should only be run with those specific users.
     * @return True if focus should be enabled, otherwise false
     * @since 1.0
     */
    boolean focusEnabled() default false;

}
