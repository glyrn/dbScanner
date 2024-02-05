import conf.DbCfg;
import util.YamlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class app {
    public static void main(String[] args) {
        String file = System.getProperty("user.dir");
        DBExcutor dbExcutor = new DBExcutor();

        // 加载配置
        DbCfg dbCfg = YamlUtils.loadYaml("src/main/java/conf/DbCfg.yaml", DbCfg.class);
        // 装载

        DBScanner.getInstance().startWork(dbCfg);

    }

}
