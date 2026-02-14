package com.github.fanzezhen.fun.framework.core.model.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 格式化
 */
public class FormatUtil {
    private FormatUtil() {
    }
    
    public static String normal(Object o) {
        return normal(o, null);
    }
    public static String normal(Object o, String nullToDefault) {
        return switch (o) {
            case null -> nullToDefault;
            case LocalDateTime localDateTime -> LocalDateTimeUtil.formatNormal(localDateTime);
            case Date date -> DateUtil.formatDate(date);
            default -> o.toString();
        };
    }
}
