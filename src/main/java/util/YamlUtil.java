package util;

import conf.DbCfg;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * yaml文件处理工具类
 *
 * @author Bruce Pan
 */
public class YamlUtil {

    /**
     * 根据传入的yml文件路径，读取文件内容并返回对应map
     *
     * @param yamlFilePath
     * @return
     */
    public static Map<String, Object> getMap(String yamlFilePath) {
        try{
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(new FileInputStream(yamlFilePath));

            return map;
        }catch (FileNotFoundException e) {
            System.out.println("未查找到该文件");
        }

        return null;
    }

    /**
     * 不指定路径默认从resources目录下读取application.yml文件
     * @return
     */
    public static Map<String, Object> getMap() {
        try{
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/application.yml"));

            return map;
        }catch (FileNotFoundException e) {
            System.out.println("未查找到该文件");
        }

        return null;

    }

    /**
     * 根据传入的yml文件路径，读取文件内容并返回对应yml对象
     * @param yamlFilePath
     * @return
     */
    public static Yaml getYaml(String yamlFilePath) {
        Yaml yaml = new Yaml();
        yaml.load(yamlFilePath);

        return yaml;
    }

    /**
     * 不指定路径默认从resources目录下读取DbCfg.yml文件
     * @return
     */
    public static Yaml getYaml() {
        Yaml yaml = new Yaml();
        yaml.load(System.getProperty("user.dir") + "/src/main/resources/DbCfg.yml");

        return yaml;
    }

    /**
     * 将yaml配置加载进内存
     * @param yamlFilePath
     * @param clazz
     * @return
     */
    public static DbCfg loadYaml(String yamlFilePath, Class<?> clazz) {

        Yaml yaml = null;
        FileInputStream inputStream = null;
        try {
            yaml = new Yaml();
            inputStream = new FileInputStream(yamlFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        DbCfg dbCfg = (DbCfg) yaml.loadAs(inputStream, clazz);
        return dbCfg;
    }

    /**
     * 默认读取 从resources目录下读取DbCfg.yml文件
     * @return
     */
    public static DbCfg loadYaml() {
        return loadYaml(System.getProperty("user.dir") + "/src/main/resources/DbCfg.yaml", DbCfg.class);
    }
}
