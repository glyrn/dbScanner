package logs;

/**
 * 日志接口
 * @author gly
 */
public interface Logger {
    public void log(LogLevel logLevel, String msg);

    public void log(LogLevel logLevel, String msg, Object... args);
}
