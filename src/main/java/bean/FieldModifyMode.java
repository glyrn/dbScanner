package bean;

/**
 * 修改字段的对比模式 使用在 modify column
 *
 */
public enum FieldModifyMode {
    SKIP,       //跳过检查
    ONLY_CORE,  //只检查核心字段值
    STRICT,     //严格匹配
}
