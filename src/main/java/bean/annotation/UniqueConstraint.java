package bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 移植api
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueConstraint {
    String name() default "";

    String[] columnNames();
}