package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常处理器
 *
 * @author guolinyun
 * @date 2024/2/5
 */
public class ExceptionUtil extends Exception{

    // 异常处理器
    Logger log = LoggerFactory.getLogger(ExceptionUtil.class);

    // 提供无参构造器

    public ExceptionUtil() {
        super();
    }


    public void catchException(Exception ex) {
        log.error("catch error: {}", ex);
    }
}


/**
 * ex todoList
 *
 * 1. print er
 *
 * 2. io into logger file
 *
 * 3. update this record to oss
 *
 */