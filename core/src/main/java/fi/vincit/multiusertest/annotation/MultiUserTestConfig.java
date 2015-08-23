package fi.vincit.multiusertest.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import fi.vincit.multiusertest.runner.junit.framework.BlockMultiUserTestClassRunner;

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
