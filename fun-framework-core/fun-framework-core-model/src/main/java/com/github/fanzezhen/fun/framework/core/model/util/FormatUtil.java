package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 格式化工具类
 * <p>
 * 提供对象的通用格式化方法，特殊处理日期类型（Date、LocalDateTime），
 * 其他类型调用toString()方法。
 */
public class FormatUtil {
    /**
     * 工具类不允许实例化
     */
    private FormatUtil() {
    }

    /**
     * 格式化对象为字符串
     * <p>
     * 如果对象为 null 则返回 null
     * </p>
     *
     * @param o 待格式化的对象
     * @return 格式化后的字符串
     */
    public static String normal(Object o) {
        return normal(o, null);
    }

    /**
     * 格式化对象为字符串（支持自定义 null 值默认值）
     * <p>
     * 特殊处理：
     * - LocalDateTime：格式化为 yyyy-MM-dd HH:mm:ss
     * - Date：格式化为 yyyy-MM-dd
     * - 其他类型：调用 toString() 方法
     * </p>
     *
     * @param o             待格式化的对象
     * @param nullToDefault 对象为 null 时返回的默认值
     * @return 格式化后的字符串
     */
    public static String normal(Object o, String nullToDefault) {
        return switch (o) {
            case null -> nullToDefault;
            case LocalDateTime localDateTime -> LocalDateTimeUtil.formatNormal(localDateTime);
            case Date date -> DateUtil.formatDate(date);
            default -> o.toString();
        };
    }
}
