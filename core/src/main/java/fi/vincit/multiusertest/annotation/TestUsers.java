package fi.vincit.multiusertest.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.runners.model.FrameworkMethod;

import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.UserIdentifier;


/**
 * This annotation works with MultiUserTestRunner. The creators parameter defines which
 * roles are used to create content and users parameter defines which roles are used to
 * consume content. The syntax and possible values for defining users are same in
 * {@link MultiUserTestRunner} was described.
 *
 * The default test runner class can be changed by using {@link TestUsers#runner()}. The class has
 * to have a constructor with:
 * <ul>
 *     <li>class: Test class</li>
 *     <li>UserIdentifier: used creator identifier</li>
 *     <li>UserIdentifier: used user identifier</li>
 * </ul>
 *
 * When creating test class instance is created, the runner class should se used user roles
 * with {@link fi.vincit.multiusertest.test.AbstractUserRoleIT#setUsers(UserIdentifier, UserIdentifier)} method.
 * Overriding {@link org.junit.runners.BlockJUnit4ClassRunner#testName(FrameworkMethod)} and
 * {@link org.junit.runners.BlockJUnit4ClassRunner#getName()} is recommended to make the test names to
 * describe all the different combinations properly.
 *
 * Annotation can also be user with methods. Then the annotation will define with what users the method
 * will be executed. If {@link TestUsers#creators()} are set, the method will only be executed if any of the specified creators are
 * being used as the creator. Same applies for the {@link TestUsers#users()}. If both creators and users are defined then
 * the method will be run only if any combination of creators and users are being used.
 */
@Target({TYPE, METHOD})
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

    /**
     * Creator roles/users to be used
     */
    String[] creators() default {};

    /**
     * User roles/users to be used
     */
    String[] users() default {};

    /**
     * Test runner class to use
     */
    Class runner() default BlockMultiUserTestClassRunner.class;

    /**
     * Default class type for failed tests
     */
    Class<? extends Throwable> defaultException() default IllegalStateException.class;
}
