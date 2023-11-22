package util;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * yaml文件处理工具类
 *
 * @author Bruce Pan
 */
public class YamlUtils {

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
     * 不指定路径默认从resources目录下读取application.yml文件
     * @return
     */
    public static Yaml getYaml() {
        Yaml yaml = new Yaml();
        yaml.load(System.getProperty("user.dir") + "/src/main/resources/application.yml");

        return yaml;
    }

}
