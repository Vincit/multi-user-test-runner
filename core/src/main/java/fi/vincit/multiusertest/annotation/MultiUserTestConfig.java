package fi.vincit.multiusertest.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.runners.model.FrameworkMethod;

import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * <p>
 * Configure test class to use desired runner and change the default expected
 * exception. The default test runner class can be changed by using {@link #runner()}.
 * Custom runners can be used. The runner class has to have a constructor with:
 * <ul>
 *     <li>{@link }java.lang.Class}: Test class</li>
 *     <li>{@link fi.vincit.multiusertest.util.UserIdentifier}: used creator identifier</li>
 *     <li>{@link fi.vincit.multiusertest.util.UserIdentifier}: used user identifier</li>
 * </ul>
 *</p>
 *
 * <p>
 * When test class instance is created, the runner class should set user roles
 * with {@link fi.vincit.multiusertest.test.AbstractUserRoleIT#setUsers(UserIdentifier, UserIdentifier)}
 * method. Overriding {@link org.junit.runners.BlockJUnit4ClassRunner#testName(FrameworkMethod)} and
 * {@link org.junit.runners.BlockJUnit4ClassRunner#getName()} is recommended to make the test names to
 * describe all the different combinations properly.
 * </p>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface MultiUserTestConfig {
    /**
     * Test runner class to use
     */
    Class runner() default BlockMultiUserTestClassRunner.class;

    /**
     * Default class type for failed tests
     */
    Class<? extends Throwable> defaultException() default IllegalStateException.class;
}
