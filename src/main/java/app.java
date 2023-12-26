import conf.DbCfg;
import lombok.extern.slf4j.Slf4j;
import util.YamlUtils;

public class app {
    public static void main(String[] args) {
        String file = System.getProperty("user.dir");
        DBExcutor dbExcutor = new DBExcutor();

        // 加载配置
        DbCfg dbCfg = YamlUtils.loadYaml("src/main/java/conf/DbCfg.yaml", DbCfg.class);
        // 装载
        dbExcutor.init(dbCfg);

        dbExcutor.getAllTablesName();

    }
}
