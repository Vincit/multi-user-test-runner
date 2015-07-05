package fi.vincit.multiusertest.annotation;

import fi.vincit.multiusertest.runner.SpringMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.runners.model.FrameworkMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

;

/**
 * This annotation works with MultiUserTestRunner. The creators parameter defines which
 * roles are used to create content and users parameter defines which roles are used to
 * consume content. The syntax and possible values for defining users are same in
 * {@link fi.vincit.multiusertest.runner.MultiUserTestRunner} was described.
 *
 * The default test runner class can be changed by using {@link this#runner()}. The class has
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
    Class runner() default SpringMultiUserTestClassRunner.class;
}
