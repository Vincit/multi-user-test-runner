package fi.vincit.multiusertest.annotation;

import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * <p>
 * Configure the test class to use the desired runner and change the default expected
 * exception. The default test class runner can be changed by using the {@link #runner()}
 * property. This annotation can be inherited so it can be set for a base class.
 * </p>
 *
 * <p>
 * If the provided class runners don't suit the needs custom class runner can be created.
 * Helper class {@link fi.vincit.multiusertest.util.RunnerDelegate} implements the basic
 * functionality of the class runner.
 * The runner class has to have a constructor with:
 * </p>
 * <ul>
 *     <li>{@link java.lang.Class}: Test class</li>
 *     <li>{@link fi.vincit.multiusertest.util.UserIdentifier}: used producer identifier</li>
 *     <li>{@link fi.vincit.multiusertest.util.UserIdentifier}: used consumer identifier</li>
 * </ul>
 * <p>
 * For more information please see the existing implementations.
 * </p>
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface MultiUserTestConfig {
    /**
     * Test class runner to use
     * @return Test runner
     */
    Class runner() default BlockMultiUserTestClassRunner.class;

}
