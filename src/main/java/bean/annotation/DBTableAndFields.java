package bean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扫表 [ 兼容老代码的注解 ]
 * 完整的包含table和所有字段的延伸描述
 *
 *
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
    DBScanTable table();

    /**
     * 只有列生效
     *
     * @return
     */
    boolean justColumns() default false;

    /**
     * 表中字段约束信息 -- 存储旧的类 用来兼容老代码
     *
     * @return
     */
    DBScanColumn[] columns();
}
