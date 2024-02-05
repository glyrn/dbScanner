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
@Table(name = "children")
public class Children {
    @Id
    @GeneratedValue(generator = "AUTO_INCREMENT")
    @Column(name = "id", unique = true)
    private int id;

    @Column(name = "egg")
    private int egg;

    @Column(name = "pigs")
    private int pigs;

    @Column(name = "oaa")
    private int oaa;


}
