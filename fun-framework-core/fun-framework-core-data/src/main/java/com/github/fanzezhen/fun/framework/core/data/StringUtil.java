package com.github.fanzezhen.fun.framework.core.data;

import cn.hutool.core.text.CharSequenceUtil;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 字符串工具类
 *
 * @author fanzezhen
 * @since 3.4.3.5
 */
public class StringUtil {
    /**
     * 移除字符串前缀，优先移除最长的前缀
     */
    public static String removeAnyPrefix(String str, String... prefixes) {
        if (CharSequenceUtil.isNotEmpty(str)) {
            for (String prefix : Arrays.stream(prefixes).sorted(Comparator.comparingInt(String::length).reversed()).toArray(String[]::new)) {
                if (str.startsWith(prefix)) {
                    return str.substring(prefix.length());
                }
            }
        }
        return str;
    }
}
