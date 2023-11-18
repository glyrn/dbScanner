package logs;

/**
 * 日志等级
 * @author gly
 */
public enum LogLevel {
    INFO("正常", "记录正常信息"),
    WARNING("警告", "记录异常但是不影响程序的信息"),
    ERROR("错误", "记录严重错误错误");

    // 日志级别
    private final String levelName;
    // 日志级别对应描述
    private final String description;
    LogLevel(String levelName, String description) {
        this.levelName = levelName;
        this.description = description;
    }
}
