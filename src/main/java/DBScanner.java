import conf.DbCfg;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用mybatis框架的时候不会自动创建表 如果数据库中没有表 应该先自动创建表并存储md5 如果已经有表了 就修改表
 */
@Slf4j
public class DBScanner {

    /**
     * 记录各个表的md5的表
     */
    public static final String TABLE_MD5 = "table_md5";

    /**
     * 全局实例对象
     */
    private static DBScanner instance = null;

    /**
     * className - md5
     */
    private Map<String, String> oldFileMd5Map = new ConcurrentHashMap<>();

    /**
     * 构造
     */
    private DBScanner() {
    }

    /**
     * 开始扫表
     * @return
     */
    public boolean startWork(DbCfg dbCfg) {
        try(DBExcutor dbExcutor = new DBExcutor()){
            // 建立数据库连接失败
            if (!dbExcutor.init(dbCfg)) {
                return false;
            }
            // 扫表

            /**
             * 1. 先扫描数据库中现存的表的信息
             * 2. 扫描所有需要处理的entity类
             * 3.
             */
//            List<String> tableNames = dbExcutor.getTables();


        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }
}
