package bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扫表专用
 * 完整的包含table和所有字段的延伸描述 [ 兼容层 ]
 *
 * @author guolinyun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DBTableAndFields {
    /**
     * table信息
     *
     * @return
     */
    TableExt table();

    /**
     * 只有列生效
     *
     * @return
     */
    boolean justColumns() default false;

    /**
     * 表中字段约束信息
     *
     * @return
     */
    ColumnExt[] columns();
}
