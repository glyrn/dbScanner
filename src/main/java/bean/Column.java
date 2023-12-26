package bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    // 列名
    String name() default "";
    // 字段类型
    FieldType type() default  FieldType.NULL;
    // 字段大小
    int size() default 0;
    // 默认值
    String defaultValue() default "";
}
