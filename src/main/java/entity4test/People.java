package entity4test;

import bean.Column;
import bean.Entity;
import bean.GeneratedValue;
import bean.Id;
import bean.Table;

/**
 * 测试用 后续这个文件夹需要删除
 */
@Entity
@Table(name = "people")
public class People {
//    @Column(name = "age", type = FieldType.INT, size = 2, defaultValue = "1")

    @Id
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private int id = 0;

    @Column(name = "age")
    private int age;

    @Column(name = "ali")
    private int ali;

    @Column(name = "tx")
    private int tx;
}
