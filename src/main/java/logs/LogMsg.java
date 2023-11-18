package logs;

import lombok.Data;

import java.util.Date;

/**
 * 日志消息体
 *
 * @author gly
 */
@Data
public class LogMsg {
    private LogLevel logLevel;
    private String msg;
    private Date date;
    public LogMsg(LogLevel logLevel, String msg) {
        this.logLevel = logLevel;
        this.msg = msg;
        // 当前时间
        this.date = new Date();
    }
}
