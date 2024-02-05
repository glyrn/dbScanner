package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * 将一个String字符串变量根据编码类型保存成文件
 *
 * @author guolinyun
 * @date 2024/2/5
 */
public class FileUtil {
    public static boolean saveAsFile(String fileContent, String filePath, String charset){
        try{
            makeSureFileName(filePath);

            File file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent);
            bw.close();
            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 从文件路径获取文件路径
     *
     * @param fullName
     * @return
     */
    public static String splitFilePath(String fullName) {
        String path = "";
        fullName = fullName.replace("\\", "/");
        String[] items = fullName.split("/");
        for (int i = 0; i < items.length - 1; i++) {
            path += items[i];
            path += "/";
        }
        return path;
    }

    /**
     * 创建目录树
     *
     * @param dirName
     * @return
     */
    public static boolean createDir(String dirName) {
        if (dirName.length() <= 0)
            return true;

        String sysDir = dirName.replace("\\", "/");
        File file = new File(sysDir);
        return file.mkdirs();
    }

    /**
     * 保证文件名前缀目录树存在
     *
     * @param fileName
     */
    public static void makeSureFileName(String fileName) {
        String folderPath = splitFilePath(fileName);
        createDir(folderPath);
    }

}
