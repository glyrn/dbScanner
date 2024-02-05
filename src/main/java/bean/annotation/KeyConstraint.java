package bean.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 索引 联合索引
 *
 * @Author guolinyun
 * @Date 2024/2/1
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyConstraint {
    String name() default "";

    String[] columnNames();

    /**
     * 唯一索引
     */
    boolean unique() default false;
}
