import conf.DbCfg;
import org.yaml.snakeyaml.Yaml;
import source.DBExcutor;
import source.DBScanner;
import util.YamlUtil;

public class app {
    public static void main(String[] args) {
//        String file = System.getProperty("user.dir");
//        DBExcutor dbExcutor = new DBExcutor();



        // 加载配置
//        DbCfg dbCfg = YamlUtil.loadYaml("src/main/java/conf/DbCfg.yaml", DbCfg.class);

//        DbCfg dbCfg = YamlUtil.loadYaml();
//
//        // 装载
//
//        DBScanner.getInstance().startWork(dbCfg);

//        Logger logger = LoggerFactory.getLogger(app.class);
//
//        logger.info("scanning table {}", "table_1");

        DBScanner.getInstance().startWork();

    }

}
