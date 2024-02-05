package bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表注解 后续兼容其他orm的相关注解
 *
 * 1. 单独标注表名
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";

    String catalog() default "";

    String schema() default "";

    UniqueConstraint[] uniqueConstraints() default {};
}
