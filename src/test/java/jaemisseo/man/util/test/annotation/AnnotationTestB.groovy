package jaemisseo.man.util.test.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@interface AnnotationTestB {

    String value() default ''
}
