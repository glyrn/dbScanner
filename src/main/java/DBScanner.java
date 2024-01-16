import conf.DbCfg;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

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
     * tableName - className
     * 作废的表
     */
    private Map<String, String> noUseTableMap = new ConcurrentHashMap<>();

    /**
     * todo 1. 开关 删除无用的表 2. 自动创建或者删除索引 3. modify column时严格检查开关 4. modify column 是否修改已存在的列
     */

    /**
     * 跳过的table 忽略检查
     */
    private Set<String> skipTable = new HashSet<>();

    /**
     * 获取实例对象
     */
    public static DBScanner getInstance() {
        if (instance == null) {
            instance = new DBScanner();
        }

        return instance;
    }

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

    /**
     * 扫描数据库
     * @param dbExcutor
     * @param tablePackages
     * @return
     */
    private boolean scanningDatabase(DBExcutor dbExcutor, String tablePackages) {
        try{

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    /**
     * 扫描所有需要处理的entity类
     * @param tablePackages
     * @return
     */
    private boolean scanningEntity(String tablePackages) {
        try{

        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }
}
