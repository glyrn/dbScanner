package util;

/**
 * 字符串工具类
 *
 * @author guolinyun
 * @date 2024/2/1
 */
public class StringUtil {
    public static String join(String[] array, String separator, int startIndex, int endIndex) {
        if (array == null || startIndex < 0 || endIndex > array.length || startIndex > endIndex) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                sb.append(separator);
            }
            if (array[i] != null) {
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }
}
