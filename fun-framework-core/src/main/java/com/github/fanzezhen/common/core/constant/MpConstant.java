package com.github.fanzezhen.fun.framework.core.constant;

/**
 * @author zezhen.fan
 */
@SuppressWarnings("unused")
public class MpConstant {
    /**
     * 默认的包内文件夹名
     */
    public static final String DEFAULT_DIR_JAVA = "/src/main/java";
    public static final String DEFAULT_DIR_MAPPER = "/src/main/resources/mapper/";
    /**
     * 数据表中的逻辑删除字段
     */
    public static final String DEFAULT_LOGIC_DELETE_COLUMN_NAME = "del_flag";
    public static final String DEFAULT_FORMAT_FILE_NAME = "%sPo";
    /**
     * 数据表中的版本号字段
     */
    public static final String VERSION_FIELD_NAME = "version";
    /**
     * 输入表名的默认提示语
     */
    public static final String DEFAULT_TABLE_NAME_INPUT_HINT = "表名";
    public static final String[] DEFAULT_IGNORE_TABLE_PREFIX = new String[]{"t_", "c_"};
    
    private MpConstant(){}
}
