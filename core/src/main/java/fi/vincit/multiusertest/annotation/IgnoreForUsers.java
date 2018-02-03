package fi.vincit.multiusertest.annotation;

import fi.vincit.multiusertest.rule.EmptyUserDefinitionClass;
import fi.vincit.multiusertest.rule.UserDefinitionClass;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation used for ignoring method with the defined
 * consumer and producer user identifiers. Annotation has to
 * be added for the method that needs to ignore the user identifiers.
 * If no consumers or producers are given, the effect is the same as
 * there wouldn't be a {@link IgnoreForUsers} annotation on the method.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface IgnoreForUsers {

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
     */
    Class<? extends UserDefinitionClass> producerClass() default EmptyUserDefinitionClass.class;

    /**
     * Consumer roles/users to be used from a user definition class
     * @return Class defining user definitions
     */
    Class<? extends UserDefinitionClass> consumerClass() default EmptyUserDefinitionClass.class;

}
