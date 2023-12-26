package util;

import conf.DbCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class b {
    public static void main(String[] args) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        FileInputStream input = new FileInputStream("src/main/java/conf/DbCfg.yaml");
        Logger logger = LoggerFactory.getLogger(b.class);
        logger.info("hello ?");
        DbCfg dbCfg = yaml.loadAs(input, DbCfg.class);

    }
}
