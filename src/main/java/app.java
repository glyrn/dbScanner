import conf.DbCfg;
import util.YamlUtils;

public class app {
    public static void main(String[] args) {
        String file = System.getProperty("user.dir");
        DbExcutor dbExcutor = new DbExcutor();

        // 加载配置
        DbCfg dbCfg = YamlUtils.loadYaml("src/main/java/conf/DbCfg.yaml", DbCfg.class);
        // 装载
        dbExcutor.init(dbCfg);

    }
}
