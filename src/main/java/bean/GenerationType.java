package bean;

/**
 * 自增类型
 *
 * @author guolinyun
 * @date 2024/2/2
 */
public enum GenerationType {
    TABLE,
    SEQUENCE,
    IDENTITY,
    AUTO;

    private GenerationType() {
    }
}