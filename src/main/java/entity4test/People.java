package entity4test;

import bean.annotation.Column;
import bean.annotation.Entity;
import bean.annotation.GeneratedValue;
import bean.annotation.Id;
import bean.annotation.Table;

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



    @Column(name = "ali")
    private String ali;

    @Column(name = "tx")
    private int tx;
}
