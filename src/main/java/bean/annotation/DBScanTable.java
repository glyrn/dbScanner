package bean.annotation;

import bean.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * table entity 的拓展注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBScanTable {
    /**
     * 表名字
     */
    String name() default "";
    /**
     * 字段排序算法
     */
    String collate() default FieldType.DEFAULT_CHARSET_COLLATE;

    /**
     * 普通索引
     */
    KeyConstraint[] keyConstraints() default {};
}
