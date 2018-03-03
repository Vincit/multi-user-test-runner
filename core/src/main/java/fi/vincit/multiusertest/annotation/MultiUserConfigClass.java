package fi.vincit.multiusertest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the property for the test class' configuration. The configuration
 * class has to extend the {@link fi.vincit.multiusertest.test.AbstractMultiUserConfig}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MultiUserConfigClass {
}
