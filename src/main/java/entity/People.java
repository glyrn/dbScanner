package entity;

import bean.Column;
import bean.FieldType;
import bean.TableName;

/**
 * 测试用 后续这个文件夹需要删除
 */
@TableName(name = "people")
public class People {
    @Column(name = "age", type = FieldType.INT, size = 2, defaultValue = "1")
    private int age;
}
