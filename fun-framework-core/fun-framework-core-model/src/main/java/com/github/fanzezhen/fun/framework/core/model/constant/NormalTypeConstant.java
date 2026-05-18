package com.github.fanzezhen.fun.framework.core.model.constant;

/**
 * 通用类型常量.
 * <p>
 * 定义框架中常用的基础类型常量，包括：
 * <ul>
 *   <li>数值常量：缓冲区大小等</li>
 *   <li>字符串常量：字段名、后缀等</li>
 * </ul>
 */
public final class NormalTypeConstant {
    /**
     * 数字常量 365.
     */
    public static final int INT_365 = 365;
    /**
     * 数字常量 1024.
     * <p>
     * 用于文件IO、网络传输等场景的标准缓冲区大小（1KB）。
     */
    public static final int INT_1024 = 1024;
    /**
     * 12小时的毫秒数.
     */
    public static final int INT_TWELVE_HOURS_MILLIS = 12 * 60 * 60 * 1000;

    /**
     * 秒转毫秒倍数.
     */
    public static final int INT_MILLIS_PER_SECOND = 1000;

    /**
     * 一分钟的秒数.
     */
    public static final int INT_ONE_MINUTE_SECONDS = 60;

    /**
     * 一小时的秒数.
     */
    public static final int INT_ONE_HOUR_SECONDS = 60 * 60;

    /**
     * 一小时的毫秒数.
     */
    public static final int INT_ONE_HOUR_MILLIS = INT_ONE_HOUR_SECONDS * 1000;

    /**
     * 一小时的毫秒数.
     */
    public static final long LONG_ONE_HOUR_MILLIS = INT_ONE_HOUR_MILLIS;
    /**
     * 一小时的毫秒数.
     */
    public static final long LONG_1000 = 1000;

    /**
     * 记录列表字段名.
     * <p>
     * 用于分页查询结果中的记录列表字段。
     */
    public static final String STR_RECORDS = "records";

    /**
     * 计数字段后缀.
     * <p>
     * 用于拼接计数相关的字段名，例如：total_count。
     */
    public static final String STR_UNDERLINE_COUNT = "_count";

    /**
     * 工具类不允许实例化.
     */
    private NormalTypeConstant() {
        throw new UnsupportedOperationException("Utility class");
    }
}
