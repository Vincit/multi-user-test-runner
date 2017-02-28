package fi.vincit.multiusertest.runner.junit5.annotation;

import org.junit.jupiter.api.TestTemplate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;

@Target({ METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
public @interface MutrTest {
}
