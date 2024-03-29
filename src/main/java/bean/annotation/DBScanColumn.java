package bean.annotation;

import bean.FieldModifyMode;
import bean.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static bean.FieldModifyMode.STRICT;

/**
 * 列约束注解
 * entity 的列注解
 *
 * @author guolinyun
 * @date 2024/2/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DBScanColumn {
    /*************** 核心字段 *****************/
    /**
     * 默认不同填 只有在批量扫库注解中使用
     * @return
     */
    String name() default "";
    // 表中字段约束信息，（类型，长度，索引，是否为空，默认值）
    FieldType type() default FieldType.NULL;	// 字段类型
    int size() default 0;					// 长度
    String collate() default "";			// 字段默认值


    /*************** 次要字段 *****************/
    String comment() default "";			// 字段注释
    String defaults() default "NULL";	    // 字段默认值 $开头表示特殊函数
    String extra() default "";              //特殊语句
    boolean hasDefaults() default true;     // 是否有默认值


    /*************** 辅助字段 *****************/
    boolean index() default false;			        // 表示索引
    FieldModifyMode modifyMode() default STRICT;  // 修改字段的对比模式 用在modify column

    /**************** 整合字段 ****************/
    boolean nullable() default true; // 表示非空

    boolean unique() default false; // 表示唯一
}
